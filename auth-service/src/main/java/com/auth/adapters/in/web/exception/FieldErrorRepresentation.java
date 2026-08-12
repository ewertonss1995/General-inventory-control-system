package com.auth.adapters.in.web.exception;

public record FieldErrorRepresentation(
    String field,
    String message
) {}