package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;

public interface AuthenticateUserPort {
    TokenResponse userLogin(LoginRequest loginRequest);
}
