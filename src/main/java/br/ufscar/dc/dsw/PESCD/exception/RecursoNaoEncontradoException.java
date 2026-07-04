package br.ufscar.dc.dsw.PESCD.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    public static final String MESSAGE_KEY = "error.recurso.nao.encontrado";

    public RecursoNaoEncontradoException() {
        super(MESSAGE_KEY);
    }
}
