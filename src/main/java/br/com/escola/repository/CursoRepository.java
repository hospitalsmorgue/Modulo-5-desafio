package br.com.escola.repository;

import br.com.escola.model.Curso;
import br.com.escola.util.JPAUtil;

import java.util.List;
import java.util.Optional;

/**
 * Persistência de cursos. A busca por nome aceita um trecho, sem diferenciar maiúsculas.
 */
public class CursoRepository {

    public Curso salvar(Curso curso) {
        return JPAUtil.executar(em -> {
            em.persist(curso);
            return curso;
        });
    }

    public List<Curso> listarTodos() {
        return JPAUtil.executar(em -> em.createQuery(
                        "SELECT c FROM Curso c ORDER BY c.nome, c.id", Curso.class)
                .getResultList());
    }

    public Optional<Curso> buscarPorId(Long id) {
        return JPAUtil.executar(em -> Optional.ofNullable(em.find(Curso.class, id)));
    }

    public List<Curso> buscarPorNomeContendo(String termo) {
        return JPAUtil.executar(em -> em.createQuery(
                        "SELECT c FROM Curso c WHERE LOWER(c.nome) LIKE :termo ORDER BY c.nome, c.id",
                        Curso.class)
                .setParameter("termo", "%" + termo.toLowerCase() + "%")
                .getResultList());
    }
}
