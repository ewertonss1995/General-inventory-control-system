package com.auth.adapters.in.web.dto;

public record TokenResponse(
    String accessToken,
    String tokenType,
    long expiresInSeconds
) {}