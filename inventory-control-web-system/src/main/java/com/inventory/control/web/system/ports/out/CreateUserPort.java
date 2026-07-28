package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;

public interface CreateUserPort {
    void createUser(RegisterUserRequest userRequest);
}