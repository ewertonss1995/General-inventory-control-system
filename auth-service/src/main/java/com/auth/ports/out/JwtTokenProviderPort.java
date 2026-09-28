package com.auth.ports.out;

import com.auth.domain.model.User;
import java.security.interfaces.RSAPublicKey;

public interface JwtTokenProviderPort {
    String generateToken(User user);
    RSAPublicKey getPublicKey();
}