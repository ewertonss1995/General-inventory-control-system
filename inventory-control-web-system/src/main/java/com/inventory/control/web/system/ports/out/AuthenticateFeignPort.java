package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;

public interface AuthenticateFeignPort {
    TokenUser userLogin(LoginUser loginUser);
    void createUser(RegisterUser registerUser);
}
