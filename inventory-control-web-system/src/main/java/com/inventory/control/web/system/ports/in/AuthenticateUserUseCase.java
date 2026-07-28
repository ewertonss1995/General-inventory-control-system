package com.inventory.control.web.system.ports.in;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;

public interface AuthenticateUserUseCase {
    TokenResponse execute(LoginRequest loginRequest);
}