package com.inventory.control.system.adapters.in.web.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    int status,
    String title,
    String message,
    Instant timestamp,
    List<FieldError> errors
) {
    public record FieldError(String field, String message) {}

    public ErrorResponse(int status, String title, String message) {
        this(status, title, message, Instant.now(), List.of());
    }

    public ErrorResponse(int status, String title, String message, List<FieldError> errors) {
        this(status, title, message, Instant.now(), errors);
    }
}