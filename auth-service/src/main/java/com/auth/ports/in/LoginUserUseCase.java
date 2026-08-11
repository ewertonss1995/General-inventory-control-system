package com.auth.ports.in;

import com.auth.domain.model.Login;

public interface LoginUserUseCase {
    String execute(Login login);
}