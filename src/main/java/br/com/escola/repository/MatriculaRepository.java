package br.com.escola.repository;

import br.com.escola.dto.RelatorioEngajamento;
import br.com.escola.model.Matricula;
import br.com.escola.util.JPAUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistência de matrículas e do relatório agregado de engajamento.
 */
public class MatriculaRepository {

    /**
     * Totais por curso. A idade é a quantidade de anos completos (função AGE do PostgreSQL).
     * Os últimos 30 dias incluem a data de hoje e os 30 dias anteriores.
     */
    private static final String SQL_RELATORIO = """
            SELECT c.id,
                   c.nome,
                   COUNT(m.id) AS total_matriculados,
                   AVG(EXTRACT(YEAR FROM AGE(CURRENT_DATE, a.data_nascimento))) AS media_idade,
                   COUNT(m.id) FILTER (
                       WHERE m.data_matricula >= CURRENT_DATE - INTERVAL '30 days'
                   ) AS matriculados_ultimos_30_dias
            FROM curso c
            LEFT JOIN matricula m ON m.curso_id = c.id
            LEFT JOIN aluno a ON a.id = m.aluno_id
            GROUP BY c.id, c.nome
            ORDER BY c.nome, c.id
            """;

    public Matricula salvar(Matricula matricula) {
        return JPAUtil.executar(em -> {
            em.persist(matricula);
            return matricula;
        });
    }

    public List<Matricula> listarTodasComAlunoECurso() {
        return JPAUtil.executar(em -> em.createQuery(
                        "SELECT m FROM Matricula m JOIN FETCH m.aluno JOIN FETCH m.curso "
                                + "ORDER BY m.dataMatricula DESC, m.id",
                        Matricula.class)
                .getResultList());
    }

    public boolean existePorAlunoECurso(Long alunoId, Long cursoId) {
        Long total = JPAUtil.executar(em -> em.createQuery(
                        "SELECT COUNT(m) FROM Matricula m "
                                + "WHERE m.aluno.id = :alunoId AND m.curso.id = :cursoId",
                        Long.class)
                .setParameter("alunoId", alunoId)
                .setParameter("cursoId", cursoId)
                .getSingleResult());
        return total != null && total > 0;
    }

    public List<RelatorioEngajamento> relatorioEngajamento() {
        return JPAUtil.executar(em -> {
            @SuppressWarnings("unchecked")
            List<Object> linhas = em.createNativeQuery(SQL_RELATORIO).getResultList();
            List<RelatorioEngajamento> relatorio = new ArrayList<>();
            for (Object linhaBruta : linhas) {
                Object[] linha = (Object[]) linhaBruta;
                Double media = linha[3] == null ? null : ((Number) linha[3]).doubleValue();
                relatorio.add(new RelatorioEngajamento(
                        ((Number) linha[0]).longValue(),
                        (String) linha[1],
                        ((Number) linha[2]).longValue(),
                        media,
                        ((Number) linha[4]).longValue()
                ));
            }
            return relatorio;
        });
    }
}
