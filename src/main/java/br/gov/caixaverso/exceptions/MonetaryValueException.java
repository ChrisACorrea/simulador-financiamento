package br.gov.caixaverso.exceptions;

public class MonetaryValueException extends RuntimeException {

    public MonetaryValueException(String message) {
        super(message);
    }

    public MonetaryValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
