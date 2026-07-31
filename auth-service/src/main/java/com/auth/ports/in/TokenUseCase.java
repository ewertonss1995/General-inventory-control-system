package com.auth.ports.in;

import com.auth.domain.model.User;

public interface TokenUseCase {
    String generateToken(User user);
}