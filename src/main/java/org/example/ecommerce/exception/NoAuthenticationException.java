package org.example.ecommerce.exception;

public class NoAuthenticationException extends RuntimeException {

    public NoAuthenticationException(String message) {
        super(message);
    }

    public NoAuthenticationException() {
        this("No authentication");
    }
}
