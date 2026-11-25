package br.edu.utfpr.pesagem.exception;

/** Violação de regra de negócio (RNxx) -> HTTP 422. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) { super(mensagem); }
}
