package com.auth.ports.out;

import com.auth.domain.model.User;

public interface JwtTokenProviderPort {
    String generateToken(User user);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}