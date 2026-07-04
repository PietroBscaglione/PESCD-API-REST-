package br.ufscar.dc.dsw.PESCD.exception;

public class CsvInvalidoException extends RuntimeException {
    private final String messageKey;

    public CsvInvalidoException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
