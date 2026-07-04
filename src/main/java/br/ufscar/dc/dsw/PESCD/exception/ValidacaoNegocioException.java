package br.ufscar.dc.dsw.PESCD.exception;

public class ValidacaoNegocioException extends RuntimeException {
    private final String messageKey;

    public ValidacaoNegocioException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
