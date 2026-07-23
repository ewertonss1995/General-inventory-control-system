package com.auth.ports.in;

import com.auth.adapters.in.web.dto.RegisterRequest;

public interface RegisterUserUseCase {
    void execute(RegisterRequest request);
}