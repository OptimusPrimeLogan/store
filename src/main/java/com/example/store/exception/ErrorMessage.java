package com.example.store.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private int statusCode;
    private Instant timestamp;
    private String message;
    private String description;
}
