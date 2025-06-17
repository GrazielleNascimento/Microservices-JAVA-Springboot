package br.com.petsgft.api.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super("The requested resource was not found: " + message);
    }
}