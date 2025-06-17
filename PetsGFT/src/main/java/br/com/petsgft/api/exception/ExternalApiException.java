package br.com.petsgft.api.exception;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message, Throwable cause) {
        super("An error occurred while communicating with an external API: " + message, cause);
    }
}