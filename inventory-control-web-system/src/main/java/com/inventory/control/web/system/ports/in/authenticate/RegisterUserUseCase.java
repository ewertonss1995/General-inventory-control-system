package com.inventory.control.web.system.ports.in.authenticate;

import com.inventory.control.web.system.domain.model.RegisterUser;

public interface RegisterUserUseCase {
    void execute(RegisterUser registerUser);
}