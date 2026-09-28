package com.auth.infrastructure.config;

import com.auth.domain.service.LoginUserService;
import com.auth.domain.service.RegisterUserService;
import com.auth.domain.service.TokenService;
import com.auth.infrastructure.decorator.TransactionalRegisterUserDecorator;
import com.auth.ports.in.RegisterUserUseCase;
import com.auth.ports.in.LoginUserUseCase;
import com.auth.ports.in.TokenUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth.ports.out.UserRepositoryPort;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.ports.out.JwtTokenProviderPort;
import com.auth.ports.out.PasswordEncoderPort;

@Configuration
public class BeanConfig {
    
    @Bean
    public LoginUserUseCase loginUserUseCase(
        UserRepositoryPort userRepositoryPort,
        PasswordEncoderPort passwordEncoderPort, 
        TokenUseCase tokenUseCase) {
        
        return new LoginUserService(
            userRepositoryPort, passwordEncoderPort, tokenUseCase);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
        RoleRepositoryPort roleRepositoryPort,
        UserRepositoryPort userRepositoryPort,
        PasswordEncoderPort passwordEncoderPort) {

        RegisterUserUseCase domainService = new RegisterUserService(
                roleRepositoryPort, 
                userRepositoryPort, 
                passwordEncoderPort
        );

        return new TransactionalRegisterUserDecorator(domainService);
    }

    @Bean
    public TokenUseCase tokenUseCase(JwtTokenProviderPort jwtTokenProviderPort) {
        return new TokenService(jwtTokenProviderPort);
    }

}
