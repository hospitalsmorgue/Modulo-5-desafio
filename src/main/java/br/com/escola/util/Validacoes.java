package br.com.escola.util;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Regras simples de formato compartilhadas pelo menu e pelos serviços.
 */
public final class Validacoes {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    public static final int TAMANHO_MAXIMO_EMAIL = 150;

    private Validacoes() {
    }

    public static String normalizarEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean emailValido(String email) {
        String normalizado = normalizarEmail(email);
        return !normalizado.isEmpty()
                && normalizado.length() <= TAMANHO_MAXIMO_EMAIL
                && EMAIL.matcher(normalizado).matches();
    }
}
