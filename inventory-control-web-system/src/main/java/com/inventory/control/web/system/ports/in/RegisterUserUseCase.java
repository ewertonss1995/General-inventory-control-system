package com.inventory.control.web.system.ports.in;

import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;

public interface RegisterUserUseCase {
    void execute(RegisterUserRequest userRequest);
}