package com.inventory.control.web.system.application.dto;

public record TokenDto(
    String accessToken,
    String tokenType,
    long expiresInSeconds
) {}