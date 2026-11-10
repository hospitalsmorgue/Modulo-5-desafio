package br.com.escola.service;

import br.com.escola.exception.RegraNegocioException;
import br.com.escola.model.Curso;
import br.com.escola.repository.CursoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Regras de cadastro e consulta de cursos.
 */
public class CursoService {

    private final CursoRepository repositorio;

    public CursoService() {
        this(new CursoRepository());
    }

    public CursoService(CursoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Curso cadastrar(String nome, String descricao, int cargaHoraria) {
        String nomeLimpo = validarNome(nome);
        String descricaoLimpa = normalizarDescricao(descricao);
        if (cargaHoraria <= 0 || cargaHoraria > 10000) {
            throw new RegraNegocioException("A carga horária deve ser um número inteiro maior que zero (até 10000).");
        }
        return repositorio.salvar(new Curso(nomeLimpo, descricaoLimpa, cargaHoraria));
    }

    public List<Curso> listar() {
        return repositorio.listarTodos();
    }

    public Optional<Curso> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repositorio.buscarPorId(id);
    }

    public List<Curso> buscarPorNome(String termo) {
        if (termo == null || termo.isBlank()) {
            throw new RegraNegocioException("Informe o nome ou parte do nome do curso.");
        }
        String termoLimpo = termo.trim();
        if (termoLimpo.length() > 120) {
            throw new RegraNegocioException("O termo de busca pode ter no máximo 120 caracteres.");
        }
        return repositorio.buscarPorNomeContendo(termoLimpo);
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome do curso é obrigatório.");
        }
        String nomeLimpo = nome.trim();
        if (nomeLimpo.length() > 120) {
            throw new RegraNegocioException("O nome do curso pode ter no máximo 120 caracteres.");
        }
        return nomeLimpo;
    }

    private String normalizarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return null;
        }
        String descricaoLimpa = descricao.trim();
        if (descricaoLimpa.length() > 500) {
            throw new RegraNegocioException("A descrição pode ter no máximo 500 caracteres.");
        }
        return descricaoLimpa;
    }
}
