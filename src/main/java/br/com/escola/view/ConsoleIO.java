package br.com.escola.view;

import br.com.escola.exception.FimDeEntradaException;
import br.com.escola.util.Validacoes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Leitura do console. Data ou número inválido pede o valor de novo, em vez de encerrar o programa.
 */
public class ConsoleIO implements AutoCloseable {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private final Scanner scanner;

    public ConsoleIO() {
        this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    }

    public String lerLinha(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        if (!scanner.hasNextLine()) {
            System.out.println();
            throw new FimDeEntradaException();
        }
        return scanner.nextLine();
    }

    public String lerTextoObrigatorio(String prompt, int tamanhoMaximo) {
        while (true) {
            String linha = lerLinha(prompt);
            if (linha.isBlank()) {
                System.out.println("Valor inválido. Este campo não pode ficar em branco.");
                continue;
            }
            String texto = linha.trim();
            if (texto.length() > tamanhoMaximo) {
                System.out.println("Texto muito longo. Use no máximo " + tamanhoMaximo + " caracteres.");
                continue;
            }
            return texto;
        }
    }

    public String lerTextoOpcional(String prompt, int tamanhoMaximo) {
        while (true) {
            String linha = lerLinha(prompt);
            String texto = linha.trim();
            if (texto.length() > tamanhoMaximo) {
                System.out.println("Texto muito longo. Use no máximo " + tamanhoMaximo + " caracteres.");
                continue;
            }
            return texto.isEmpty() ? null : texto;
        }
    }

    public String lerEmail(String prompt) {
        while (true) {
            String linha = lerLinha(prompt);
            String email = Validacoes.normalizarEmail(linha);
            if (Validacoes.emailValido(email)) {
                return email;
            }
            System.out.println("E-mail inválido. Exemplo: nome@escola.com. Informe novamente.");
        }
    }

    /**
     * @param vazioSignificaHoje Enter grava a data de hoje
     * @param rejeitarFutura     pede outra data quando o valor é posterior a hoje
     */
    public LocalDate lerData(String prompt, boolean vazioSignificaHoje, boolean rejeitarFutura) {
        while (true) {
            String linha = lerLinha(prompt);
            if (linha.isBlank()) {
                if (vazioSignificaHoje) {
                    return LocalDate.now();
                }
                System.out.println("Data inválida. Use o formato dd/MM/aaaa. Exemplo: 15/03/2001.");
                continue;
            }
            LocalDate data;
            try {
                data = LocalDate.parse(linha.trim(), FORMATO_DATA);
            } catch (DateTimeParseException ex) {
                System.out.println("Data inválida. Use o formato dd/MM/aaaa. Exemplo: 15/03/2001.");
                continue;
            }
            if (rejeitarFutura && data.isAfter(LocalDate.now())) {
                System.out.println("A data não pode ser futura. Informe novamente.");
                continue;
            }
            if (data.isBefore(LocalDate.of(1900, 1, 1))) {
                System.out.println("Data muito antiga. Informe uma data a partir de 01/01/1900.");
                continue;
            }
            return data;
        }
    }

    public int lerInteiro(String prompt) {
        while (true) {
            String linha = lerLinha(prompt);
            try {
                return Integer.parseInt(linha.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Número inválido. Informe um número inteiro.");
            }
        }
    }

    public int lerInteiroPositivo(String prompt) {
        while (true) {
            int valor = lerInteiro(prompt);
            if (valor > 0 && valor <= 10000) {
                return valor;
            }
            System.out.println("Informe um número inteiro maior que zero (até 10000).");
        }
    }

    /**
     * Lê um id. O valor 0 serve para cancelar a operação.
     */
    public long lerIdOuCancelar(String prompt) {
        while (true) {
            String linha = lerLinha(prompt);
            try {
                long valor = Long.parseLong(linha.trim());
                if (valor < 0) {
                    System.out.println("Número inválido. Informe um id existente ou 0 para cancelar.");
                    continue;
                }
                return valor;
            } catch (NumberFormatException ex) {
                System.out.println("Número inválido. Informe um número inteiro.");
            }
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}
