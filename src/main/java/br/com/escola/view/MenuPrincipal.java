package br.com.escola.view;

import br.com.escola.dto.RelatorioEngajamento;
import br.com.escola.exception.FimDeEntradaException;
import br.com.escola.exception.RegraNegocioException;
import br.com.escola.model.Aluno;
import br.com.escola.model.Curso;
import br.com.escola.model.Matricula;
import br.com.escola.service.AlunoService;
import br.com.escola.service.CursoService;
import br.com.escola.service.MatriculaService;
import br.com.escola.service.RelatorioService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Menus do sistema, lidos pelo Scanner e encaminhados aos serviços.
 */
public class MenuPrincipal {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AlunoService alunoService;
    private final CursoService cursoService;
    private final MatriculaService matriculaService;
    private final RelatorioService relatorioService;

    public MenuPrincipal() {
        this(new AlunoService(), new CursoService(), new MatriculaService(), new RelatorioService());
    }

    public MenuPrincipal(AlunoService alunoService, CursoService cursoService,
                         MatriculaService matriculaService, RelatorioService relatorioService) {
        this.alunoService = alunoService;
        this.cursoService = cursoService;
        this.matriculaService = matriculaService;
        this.relatorioService = relatorioService;
    }

    public void iniciar() {
        try (ConsoleIO io = new ConsoleIO()) {
            imprimirCabecalho();
            while (true) {
                try {
                    exibirMenu();
                    String opcao = io.lerLinha("Escolha uma opção: ").trim();
                    switch (opcao) {
                        case "1" -> cadastrarAluno(io);
                        case "2" -> listarAlunos();
                        case "3" -> buscarAlunoPorEmail(io);
                        case "4" -> cadastrarCurso(io);
                        case "5" -> listarCursos();
                        case "6" -> buscarCursoPorNome(io);
                        case "7" -> realizarMatricula(io);
                        case "8" -> listarMatriculas();
                        case "9" -> relatorioEngajamento();
                        case "0" -> {
                            System.out.println("Sistema encerrado. Até logo!");
                            return;
                        }
                        default -> System.out.println("Opção inválida. Digite um número do menu.");
                    }
                } catch (FimDeEntradaException ex) {
                    System.out.println("Sistema encerrado.");
                    return;
                } catch (RuntimeException ex) {
                    System.out.println("Ocorreu um erro inesperado: " + ex.getMessage());
                    ex.printStackTrace(System.out);
                }
            }
        }
    }

    private void imprimirCabecalho() {
        System.out.println("==============================================");
        System.out.println("   SISTEMA DE CONTROLE DE CURSOS");
        System.out.println("   Escola de cursos livres");
        System.out.println("==============================================");
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("---------------- MENU PRINCIPAL ----------------");
        System.out.println("1 - Cadastrar aluno");
        System.out.println("2 - Listar alunos");
        System.out.println("3 - Buscar aluno por e-mail");
        System.out.println("4 - Cadastrar curso");
        System.out.println("5 - Listar cursos");
        System.out.println("6 - Buscar curso por nome");
        System.out.println("7 - Realizar matrícula");
        System.out.println("8 - Listar matrículas");
        System.out.println("9 - Relatório de engajamento por curso");
        System.out.println("0 - Sair");
    }

    private void cadastrarAluno(ConsoleIO io) {
        System.out.println();
        System.out.println("--- Cadastrar aluno ---");
        try {
            String nome = io.lerTextoObrigatorio("Nome: ", 120);
            String email = io.lerEmail("E-mail: ");
            if (alunoService.buscarPorEmail(email).isPresent()) {
                System.out.println("Já existe um aluno cadastrado com o e-mail " + email + ".");
                return;
            }
            LocalDate nascimento = io.lerData("Data de nascimento (dd/MM/aaaa): ", false, true);
            Aluno aluno = alunoService.cadastrar(nome, email, nascimento);
            System.out.println("Aluno cadastrado com sucesso. Id: " + aluno.getId());
        } catch (RegraNegocioException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void listarAlunos() {
        System.out.println();
        System.out.println("--- Alunos cadastrados ---");
        List<Aluno> alunos = alunoService.listar();
        if (alunos.isEmpty()) {
            System.out.println("Nenhum aluno cadastrado.");
            return;
        }
        for (Aluno aluno : alunos) {
            imprimirAluno(aluno);
        }
    }

    private void buscarAlunoPorEmail(ConsoleIO io) {
        System.out.println();
        System.out.println("--- Buscar aluno por e-mail ---");
        String email = io.lerEmail("E-mail: ");
        alunoService.buscarPorEmail(email).ifPresentOrElse(
                this::imprimirAluno,
                () -> System.out.println("Nenhum aluno encontrado com o e-mail " + email + ".")
        );
    }

    private void cadastrarCurso(ConsoleIO io) {
        System.out.println();
        System.out.println("--- Cadastrar curso ---");
        try {
            String nome = io.lerTextoObrigatorio("Nome: ", 120);
            String descricao = io.lerTextoOpcional("Descrição (Enter para deixar em branco): ", 500);
            int cargaHoraria = io.lerInteiroPositivo("Carga horária (número inteiro de horas): ");
            Curso curso = cursoService.cadastrar(nome, descricao, cargaHoraria);
            System.out.println("Curso cadastrado com sucesso. Id: " + curso.getId());
        } catch (RegraNegocioException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void listarCursos() {
        System.out.println();
        System.out.println("--- Cursos cadastrados ---");
        List<Curso> cursos = cursoService.listar();
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
            return;
        }
        for (Curso curso : cursos) {
            imprimirCurso(curso);
        }
    }

    private void buscarCursoPorNome(ConsoleIO io) {
        System.out.println();
        System.out.println("--- Buscar curso por nome ---");
        String termo = io.lerTextoObrigatorio("Nome ou parte do nome: ", 120);
        List<Curso> cursos = cursoService.buscarPorNome(termo);
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso encontrado para \"" + termo + "\".");
            return;
        }
        for (Curso curso : cursos) {
            imprimirCurso(curso);
        }
    }

    private void realizarMatricula(ConsoleIO io) {
        System.out.println();
        System.out.println("--- Realizar matrícula ---");
        List<Aluno> alunos = alunoService.listar();
        List<Curso> cursos = cursoService.listar();
        if (alunos.isEmpty() || cursos.isEmpty()) {
            System.out.println("Cadastre ao menos um aluno e um curso antes de realizar uma matrícula.");
            return;
        }

        System.out.println("Alunos:");
        for (Aluno aluno : alunos) {
            System.out.printf("  %d - %s (%s)%n", aluno.getId(), aluno.getNome(), aluno.getEmail());
        }
        System.out.println("Cursos:");
        for (Curso curso : cursos) {
            System.out.printf("  %d - %s (%d h)%n", curso.getId(), curso.getNome(), curso.getCargaHoraria());
        }

        Long alunoId = escolherId(io, "Informe o id do aluno (0 para cancelar): ", true);
        if (alunoId == null) {
            System.out.println("Matrícula cancelada.");
            return;
        }
        Long cursoId = escolherId(io, "Informe o id do curso (0 para cancelar): ", false);
        if (cursoId == null) {
            System.out.println("Matrícula cancelada.");
            return;
        }
        if (matriculaService.existeMatricula(alunoId, cursoId)) {
            System.out.println("Este aluno já está matriculado neste curso.");
            return;
        }

        try {
            LocalDate data = io.lerData("Data da matrícula (dd/MM/aaaa, Enter para hoje): ", true, true);
            Matricula matricula = matriculaService.realizar(alunoId, cursoId, data);
            System.out.println("Matrícula realizada com sucesso. Id: " + matricula.getId());
        } catch (RegraNegocioException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Long escolherId(ConsoleIO io, String prompt, boolean aluno) {
        while (true) {
            long id = io.lerIdOuCancelar(prompt);
            if (id == 0) {
                return null;
            }
            boolean existe = aluno
                    ? alunoService.buscarPorId(id).isPresent()
                    : cursoService.buscarPorId(id).isPresent();
            if (existe) {
                return id;
            }
            String tipo = aluno ? "Aluno" : "Curso";
            System.out.println(tipo + " não encontrado. Informe outro id ou 0 para cancelar.");
        }
    }

    private void listarMatriculas() {
        System.out.println();
        System.out.println("--- Matrículas ---");
        List<Matricula> matriculas = matriculaService.listar();
        if (matriculas.isEmpty()) {
            System.out.println("Nenhuma matrícula realizada.");
            return;
        }
        for (Matricula matricula : matriculas) {
            System.out.printf(
                    "Id: %d | Aluno: %s | Curso: %s | Data: %s%n",
                    matricula.getId(),
                    matricula.getAluno().getNome(),
                    matricula.getCurso().getNome(),
                    matricula.getDataMatricula().format(FORMATO_DATA)
            );
        }
    }

    private void relatorioEngajamento() {
        System.out.println();
        System.out.println("--- Relatório de engajamento por curso ---");
        List<RelatorioEngajamento> linhas = relatorioService.engajamentoPorCurso();
        if (linhas.isEmpty()) {
            System.out.println("Nenhum curso cadastrado para o relatório.");
            return;
        }
        for (RelatorioEngajamento linha : linhas) {
            System.out.println("Curso: " + linha.getNomeCurso() + " (id " + linha.getCursoId() + ")");
            System.out.println("  Total de alunos matriculados: " + linha.getTotalMatriculados());
            System.out.println("  Média de idade: " + formatarMedia(linha.getMediaIdade()));
            System.out.println("  Matriculados nos últimos 30 dias: " + linha.getMatriculadosUltimos30Dias());
        }
    }

    private void imprimirAluno(Aluno aluno) {
        System.out.printf(
                "Id: %d | Nome: %s | E-mail: %s | Nascimento: %s%n",
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getDataNascimento().format(FORMATO_DATA)
        );
    }

    private void imprimirCurso(Curso curso) {
        String descricao = curso.getDescricao() == null || curso.getDescricao().isBlank()
                ? "sem descrição"
                : curso.getDescricao();
        System.out.printf(
                "Id: %d | Nome: %s | Carga horária: %d h | Descrição: %s%n",
                curso.getId(),
                curso.getNome(),
                curso.getCargaHoraria(),
                descricao
        );
    }

    private String formatarMedia(Double media) {
        if (media == null) {
            return "sem dados (nenhum aluno matriculado)";
        }
        return String.format(new Locale("pt", "BR"), "%.1f anos", media);
    }
}
