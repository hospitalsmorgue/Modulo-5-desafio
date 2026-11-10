package br.com.escola.service;

import br.com.escola.exception.RegraNegocioException;
import br.com.escola.model.Aluno;
import br.com.escola.repository.AlunoRepository;
import br.com.escola.util.Validacoes;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Regras de cadastro e consulta de alunos.
 */
public class AlunoService {

    private final AlunoRepository repositorio;

    public AlunoService() {
        this(new AlunoRepository());
    }

    public AlunoService(AlunoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Aluno cadastrar(String nome, String email, LocalDate dataNascimento) {
        String nomeLimpo = validarNome(nome);
        String emailLimpo = validarEmail(email);
        validarDataNascimento(dataNascimento);

        if (repositorio.buscarPorEmail(emailLimpo).isPresent()) {
            throw new RegraNegocioException("Já existe um aluno cadastrado com o e-mail " + emailLimpo + ".");
        }

        try {
            return repositorio.salvar(new Aluno(nomeLimpo, emailLimpo, dataNascimento));
        } catch (PersistenceException ex) {
            throw traduzirDuplicidade(ex, "Já existe um aluno cadastrado com o e-mail " + emailLimpo + ".");
        }
    }

    public List<Aluno> listar() {
        return repositorio.listarTodos();
    }

    public Optional<Aluno> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repositorio.buscarPorId(id);
    }

    public Optional<Aluno> buscarPorEmail(String email) {
        String emailLimpo = Validacoes.normalizarEmail(email);
        if (!Validacoes.emailValido(emailLimpo)) {
            throw new RegraNegocioException("Informe um e-mail válido. Exemplo: nome@escola.com.");
        }
        return repositorio.buscarPorEmail(emailLimpo);
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome do aluno é obrigatório.");
        }
        String nomeLimpo = nome.trim();
        if (nomeLimpo.length() > 120) {
            throw new RegraNegocioException("O nome do aluno pode ter no máximo 120 caracteres.");
        }
        return nomeLimpo;
    }

    private String validarEmail(String email) {
        String emailLimpo = Validacoes.normalizarEmail(email);
        if (!Validacoes.emailValido(emailLimpo)) {
            throw new RegraNegocioException("Informe um e-mail válido. Exemplo: nome@escola.com.");
        }
        return emailLimpo;
    }

    private void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new RegraNegocioException("A data de nascimento é obrigatória.");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data de nascimento não pode ser futura.");
        }
        if (dataNascimento.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new RegraNegocioException("Informe uma data de nascimento a partir de 01/01/1900.");
        }
    }

    private RuntimeException traduzirDuplicidade(PersistenceException ex, String mensagem) {
        Throwable atual = ex;
        while (atual != null) {
            String texto = atual.getMessage() == null ? "" : atual.getMessage().toLowerCase(Locale.ROOT);
            if (texto.contains("uk_aluno_email") || texto.contains("duplicate") || texto.contains("unique")) {
                return new RegraNegocioException(mensagem);
            }
            atual = atual.getCause();
        }
        return ex;
    }
}
