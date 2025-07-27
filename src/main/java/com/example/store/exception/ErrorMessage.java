package com.example.store.exception;

import java.time.Instant;
import java.util.List;

public record ErrorMessage(int statusCode, Instant timestamp, List<String> message, String description) {}
