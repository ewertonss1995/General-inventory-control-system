package com.auth.ports.in;

import com.auth.adapters.out.database.entity.UserEntity;

public interface TokenUseCase {
    String generateToken(UserEntity user);
}