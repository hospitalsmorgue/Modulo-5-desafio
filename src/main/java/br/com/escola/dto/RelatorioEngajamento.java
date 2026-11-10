package br.com.escola.dto;

/**
 * Uma linha do relatório de engajamento: totais de um curso.
 * A média de idade fica nula quando o curso ainda não tem alunos.
 */
public class RelatorioEngajamento {

    private final Long cursoId;
    private final String nomeCurso;
    private final long totalMatriculados;
    private final Double mediaIdade;
    private final long matriculadosUltimos30Dias;

    public RelatorioEngajamento(Long cursoId, String nomeCurso, long totalMatriculados,
                                Double mediaIdade, long matriculadosUltimos30Dias) {
        this.cursoId = cursoId;
        this.nomeCurso = nomeCurso;
        this.totalMatriculados = totalMatriculados;
        this.mediaIdade = mediaIdade;
        this.matriculadosUltimos30Dias = matriculadosUltimos30Dias;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public long getTotalMatriculados() {
        return totalMatriculados;
    }

    public Double getMediaIdade() {
        return mediaIdade;
    }

    public long getMatriculadosUltimos30Dias() {
        return matriculadosUltimos30Dias;
    }
}
