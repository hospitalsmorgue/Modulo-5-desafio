package br.com.escola.service;

import br.com.escola.dto.RelatorioEngajamento;
import br.com.escola.repository.MatriculaRepository;

import java.util.List;

/**
 * Relatório de engajamento por curso: total de alunos, média de idade
 * e quantidade de matrículas feitas nos últimos 30 dias.
 */
public class RelatorioService {

    private final MatriculaRepository matriculaRepository;

    public RelatorioService() {
        this(new MatriculaRepository());
    }

    public RelatorioService(MatriculaRepository matriculaRepository) {
        this.matriculaRepository = matriculaRepository;
    }

    public List<RelatorioEngajamento> engajamentoPorCurso() {
        return matriculaRepository.relatorioEngajamento();
    }
}
