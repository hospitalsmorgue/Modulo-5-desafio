package br.com.escola.repository;

import br.com.escola.model.Aluno;
import br.com.escola.util.JPAUtil;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Persistência de alunos com JPQL.
 */
public class AlunoRepository {

    public Aluno salvar(Aluno aluno) {
        return JPAUtil.executar(em -> {
            em.persist(aluno);
            return aluno;
        });
    }

    public List<Aluno> listarTodos() {
        return JPAUtil.executar(em -> em.createQuery(
                        "SELECT a FROM Aluno a ORDER BY a.nome, a.id", Aluno.class)
                .getResultList());
    }

    public Optional<Aluno> buscarPorId(Long id) {
        return JPAUtil.executar(em -> Optional.ofNullable(em.find(Aluno.class, id)));
    }

    public Optional<Aluno> buscarPorEmail(String email) {
        return JPAUtil.executar(em -> em.createQuery(
                        "SELECT a FROM Aluno a WHERE LOWER(a.email) = :email", Aluno.class)
                .setParameter("email", email.toLowerCase(Locale.ROOT))
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst());
    }
}
