package br.com.escola.exception;

/**
 * Erro de regra de negócio que deve ser mostrado ao usuário, sem derrubar o sistema.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
