package org.example.ecommerce.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(BAD_REQUEST)
    public void handleIllegalArgumentOrStateException(Exception ex) {
        System.out.println(ex.toString());
    }

    @ExceptionHandler({NoAuthenticationException.class, ExpiredJwtException.class})
    @ResponseStatus(UNAUTHORIZED)
    public void handleAuthenticationException(Exception ex) {
        System.out.println(ex.toString());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public void handleEntityNotFoundException(EntityNotFoundException ex) {
        System.out.println(ex.toString());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(CONFLICT)
    public void handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        System.out.println(ex.toString());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public void handleRuntimeException(RuntimeException ex) {
        System.out.println(ex.toString());
    }
}
