package br.gov.caixaverso.exceptions;

public class PercentageException extends RuntimeException {

    public PercentageException(String message) {
        super(message);
    }

    public PercentageException(String message, Throwable cause) {
        super(message, cause);
    }
}
