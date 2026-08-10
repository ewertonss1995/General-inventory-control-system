package com.inventory.control.web.system.ports.in.authenticate;

import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.TokenUser;

public interface LoginUserUseCase {
    TokenUser execute(LoginUser loginuser);
}