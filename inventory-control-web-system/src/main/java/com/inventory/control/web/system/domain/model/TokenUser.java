package com.inventory.control.web.system.domain.model;

public record TokenUser(
    String accessToken,
    String tokenType,
    long expiresInSeconds
) {}