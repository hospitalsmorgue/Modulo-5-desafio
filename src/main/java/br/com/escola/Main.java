package br.com.escola;

import br.com.escola.util.JPAUtil;
import br.com.escola.view.MenuPrincipal;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Ponto de entrada do sistema de controle de cursos.
 */
public class Main {

    public static void main(String[] args) {
        silenciarLogsInternos();
        try {
            JPAUtil.getEntityManager().close();
        } catch (RuntimeException ex) {
            System.out.println("Não foi possível conectar ao PostgreSQL.");
            System.out.println("Confira se o banco está no ar e se a URL, o usuário e a senha estão corretos.");
            System.out.println("Detalhe: " + causaRaiz(ex));
            JPAUtil.fechar();
            System.exit(1);
        }

        try {
            new MenuPrincipal().iniciar();
        } finally {
            JPAUtil.fechar();
        }
    }

    /**
     * O Hibernate registra detalhes técnicos no console. O menu fica só com as mensagens do sistema.
     */
    private static void silenciarLogsInternos() {
        System.setProperty("org.jboss.logging.provider", "jdk");
        LogManager.getLogManager().reset();
        Logger raiz = Logger.getLogger("");
        raiz.setLevel(Level.SEVERE);
        ConsoleHandler console = new ConsoleHandler();
        console.setLevel(Level.SEVERE);
        raiz.addHandler(console);
    }

    private static String causaRaiz(Throwable erro) {
        Throwable atual = erro;
        while (atual.getCause() != null && atual.getCause() != atual) {
            atual = atual.getCause();
        }
        String mensagem = atual.getMessage();
        return mensagem == null || mensagem.isBlank() ? atual.toString() : mensagem;
    }
}
