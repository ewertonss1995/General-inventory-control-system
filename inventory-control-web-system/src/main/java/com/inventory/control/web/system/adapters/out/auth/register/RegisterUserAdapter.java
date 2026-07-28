package com.inventory.control.web.system.adapters.out.auth.register;

import com.inventory.control.web.system.adapters.out.auth.AuthFeignClient;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import org.springframework.stereotype.Component;
import com.inventory.control.web.system.ports.out.CreateUserPort;

@Component
public class RegisterUserAdapter implements CreateUserPort {

    private final AuthFeignClient authFeignClient;

    public RegisterUserAdapter(AuthFeignClient authFeignClient) {
        this.authFeignClient = authFeignClient;
    }

    @Override
    public void createUser(RegisterUserRequest registerUserRequest) {
        authFeignClient.register(registerUserRequest);
    }
}
