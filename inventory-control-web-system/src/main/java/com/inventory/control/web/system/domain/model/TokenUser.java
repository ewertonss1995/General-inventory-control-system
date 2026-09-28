package com.inventory.control.web.system.domain.model;

public class TokenUser {
    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;

    public TokenUser() {}

    public TokenUser(String accessToken, String tokenType, long expiresInSeconds) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public void setExpiresInSeconds(long expiresInSeconds ) { this.expiresInSeconds = expiresInSeconds; }
}