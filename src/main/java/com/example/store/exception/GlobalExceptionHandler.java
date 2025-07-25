package com.example.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                // Instant.now().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                ex.getMessage(),
                request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalExceptionHandler(Exception ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                // Instant.now().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                ex.getMessage(),
                request.getDescription(false));
    }
}
