package br.com.escola.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Abre a fábrica de EntityManager e aplica URL, usuário e senha vindos do ambiente.
 * As variáveis DB_URL, DB_USER e DB_PASSWORD substituem os valores do persistence.xml
 * quando estão definidas. A senha é aplicada mesmo vazia; URL e usuário em branco são ignorados.
 */
public final class JPAUtil {

    private static final String UNIDADE_PERSISTENCIA = "escola-pu";
    private static EntityManagerFactory fabrica;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getFabrica() {
        if (fabrica == null || !fabrica.isOpen()) {
            fabrica = Persistence.createEntityManagerFactory(UNIDADE_PERSISTENCIA, propriedadesDoAmbiente());
        }
        return fabrica;
    }

    public static EntityManager getEntityManager() {
        return getFabrica().createEntityManager();
    }

    public static <T> T executar(Function<EntityManager, T> acao) {
        EntityManager em = getEntityManager();
        EntityTransaction transacao = em.getTransaction();
        try {
            transacao.begin();
            T resultado = acao.apply(em);
            transacao.commit();
            return resultado;
        } catch (RuntimeException ex) {
            if (transacao.isActive()) {
                transacao.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public static synchronized void fechar() {
        if (fabrica != null && fabrica.isOpen()) {
            fabrica.close();
        }
        fabrica = null;
    }

    private static Map<String, Object> propriedadesDoAmbiente() {
        Map<String, Object> propriedades = new HashMap<>();
        String url = System.getenv("DB_URL");
        if (url != null && !url.isBlank()) {
            propriedades.put("jakarta.persistence.jdbc.url", url.trim());
        }
        String usuario = System.getenv("DB_USER");
        if (usuario != null && !usuario.isBlank()) {
            propriedades.put("jakarta.persistence.jdbc.user", usuario.trim());
        }
        String senha = System.getenv("DB_PASSWORD");
        if (senha != null) {
            propriedades.put("jakarta.persistence.jdbc.password", senha);
        }
        return propriedades;
    }
}
