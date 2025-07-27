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

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns a 404 Not Found response.
     *
     * @param ex      the exception that was thrown
     * @param request the current web request
     * @return an ErrorMessage containing the error details
     */
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

    /**
     * Handles MethodArgumentNotValidException and returns a 400 Bad Request response with validation errors.
     *
     * @param ex      the exception that was thrown
     * @param request the current web request
     * @return an ErrorMessage containing the validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                // Instant.now().truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                errors,
                request.getDescription(false));
    }

    /**
     * Handles all other exceptions and returns a 500 Internal Server Error response.
     *
     * @param ex      the exception that was thrown
     * @param request the current web request
     * @return an ErrorMessage containing the error details
     */
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
