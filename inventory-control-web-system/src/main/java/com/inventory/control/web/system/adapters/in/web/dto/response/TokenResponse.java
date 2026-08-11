package com.inventory.control.web.system.adapters.in.web.dto.response;

public record TokenResponse(
    String accessToken,
    String tokenType,
    long expiresInSeconds
) {}