package br.com.escola.exception;

/**
 * Sinaliza que a entrada padrão acabou (por exemplo, em um teste com arquivo redirecionado).
 */
public class FimDeEntradaException extends RuntimeException {

    public FimDeEntradaException() {
        super("Fim da entrada.");
    }
}
