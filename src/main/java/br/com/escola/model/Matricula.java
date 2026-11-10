package br.com.escola.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Vínculo de um aluno com um curso. O mesmo aluno não pode se matricular duas vezes no mesmo curso.
 */
@Entity
@Table(
        name = "matricula",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_matricula_aluno_curso",
                columnNames = {"aluno_id", "curso_id"}
        )
)
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matricula_aluno"))
    private Aluno aluno;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false, foreignKey = @ForeignKey(name = "fk_matricula_curso"))
    private Curso curso;

    @Column(name = "data_matricula", nullable = false)
    private LocalDate dataMatricula;

    public Matricula() {
    }

    public Matricula(Aluno aluno, Curso curso, LocalDate dataMatricula) {
        this.aluno = aluno;
        this.curso = curso;
        this.dataMatricula = dataMatricula;
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Matricula matricula)) {
            return false;
        }
        return id != null && id.equals(matricula.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
