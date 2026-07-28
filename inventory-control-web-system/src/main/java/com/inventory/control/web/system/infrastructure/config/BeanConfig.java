package com.inventory.control.web.system.infrastructure.config;

import com.inventory.control.web.system.domain.service.AuthenticateUserService;
import com.inventory.control.web.system.domain.service.FetchProductService;
import com.inventory.control.web.system.domain.service.RegisterProductService;
import com.inventory.control.web.system.domain.service.RegisterUserService;
import com.inventory.control.web.system.ports.out.AuthenticateUserPort;
import com.inventory.control.web.system.ports.out.FetchProductPort;
import com.inventory.control.web.system.ports.out.CreateProductPort;
import com.inventory.control.web.system.ports.out.CreateUserPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public AuthenticateUserService authenticateUserService(AuthenticateUserPort authenticateUserPort) {
        return new AuthenticateUserService(authenticateUserPort);
    }

    @Bean
    public FetchProductService fetchProductService(FetchProductPort fetchProductPort) {
        return new FetchProductService(fetchProductPort);
    }

    @Bean
    public RegisterProductService registerProductService(CreateProductPort createProductPort) {
        return new RegisterProductService(createProductPort);
    }

    @Bean
    public RegisterUserService registerUserService(CreateUserPort createUserPort) {
        return new RegisterUserService(createUserPort);
    }
}
