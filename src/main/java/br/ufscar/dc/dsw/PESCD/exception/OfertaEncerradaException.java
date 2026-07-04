package br.ufscar.dc.dsw.PESCD.exception;

public class OfertaEncerradaException extends RuntimeException {
    public static final String MESSAGE_KEY = "oferta.error.encerrada";

    public OfertaEncerradaException() {
        super(MESSAGE_KEY);
    }
}
