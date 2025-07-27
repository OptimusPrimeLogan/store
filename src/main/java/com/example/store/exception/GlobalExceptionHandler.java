package com.example.store.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                // Instant.now().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                List.of(ex.getMessage()),
                request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                // Instant.now().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                errors,
                request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalExceptionHandler(Exception ex, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                // Instant.now().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                List.of(ex.getMessage()),
                request.getDescription(false));
    }
}
