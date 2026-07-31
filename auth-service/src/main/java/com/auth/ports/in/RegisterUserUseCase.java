package com.auth.ports.in;

import com.auth.domain.model.User;

public interface RegisterUserUseCase {
    void execute(User user);
}