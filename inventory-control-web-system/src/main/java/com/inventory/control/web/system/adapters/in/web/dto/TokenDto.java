package com.inventory.control.web.system.adapters.in.web.dto;

public record TokenDto(
    String accessToken,
    String tokenType,
    long expiresInSeconds
) {}