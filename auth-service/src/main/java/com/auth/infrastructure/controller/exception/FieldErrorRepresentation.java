package com.auth.infrastructure.controller.exception;

public record FieldErrorRepresentation(
    String field,
    String message
) {}