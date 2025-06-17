package br.com.petsgft.api.exception;

public class FilterException extends RuntimeException {
    public FilterException(String message, Throwable cause) {
        super("There was an issue with the provided filters: " + message, cause);
    }
}