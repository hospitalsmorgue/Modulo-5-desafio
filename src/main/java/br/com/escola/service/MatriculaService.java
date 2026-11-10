package br.com.escola.service;

import br.com.escola.exception.RegraNegocioException;
import br.com.escola.model.Aluno;
import br.com.escola.model.Curso;
import br.com.escola.model.Matricula;
import br.com.escola.repository.AlunoRepository;
import br.com.escola.repository.CursoRepository;
import br.com.escola.repository.MatriculaRepository;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Regras de matrícula: aluno e curso precisam existir e o par não pode se repetir.
 */
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    public MatriculaService() {
        this(new MatriculaRepository(), new AlunoRepository(), new CursoRepository());
    }

    public MatriculaService(MatriculaRepository matriculaRepository,
                            AlunoRepository alunoRepository,
                            CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
    }

    public Matricula realizar(Long alunoId, Long cursoId, LocalDate dataMatricula) {
        Aluno aluno = alunoRepository.buscarPorId(alunoId)
                .orElseThrow(() -> new RegraNegocioException("Aluno não encontrado."));
        Curso curso = cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new RegraNegocioException("Curso não encontrado."));

        if (dataMatricula == null) {
            throw new RegraNegocioException("A data da matrícula é obrigatória.");
        }
        if (dataMatricula.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data da matrícula não pode ser futura.");
        }
        if (dataMatricula.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new RegraNegocioException("Informe uma data de matrícula a partir de 01/01/1900.");
        }
        if (matriculaRepository.existePorAlunoECurso(alunoId, cursoId)) {
            throw new RegraNegocioException(
                    "O aluno " + aluno.getNome() + " já está matriculado no curso " + curso.getNome() + ".");
        }

        try {
            return matriculaRepository.salvar(new Matricula(aluno, curso, dataMatricula));
        } catch (PersistenceException ex) {
            Throwable atual = ex;
            while (atual != null) {
                String texto = atual.getMessage() == null ? "" : atual.getMessage().toLowerCase(Locale.ROOT);
                if (texto.contains("uk_matricula_aluno_curso") || texto.contains("duplicate") || texto.contains("unique")) {
                    throw new RegraNegocioException(
                            "O aluno " + aluno.getNome() + " já está matriculado no curso " + curso.getNome() + ".");
                }
                atual = atual.getCause();
            }
            throw ex;
        }
    }

    public boolean existeMatricula(Long alunoId, Long cursoId) {
        return matriculaRepository.existePorAlunoECurso(alunoId, cursoId);
    }

    public List<Matricula> listar() {
        return matriculaRepository.listarTodasComAlunoECurso();
    }
}
