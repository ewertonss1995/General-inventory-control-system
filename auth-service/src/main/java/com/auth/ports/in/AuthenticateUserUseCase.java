package com.auth.ports.in;

import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.TokenResponse;

public interface AuthenticateUserUseCase {
    TokenResponse execute(LoginRequest request);
}