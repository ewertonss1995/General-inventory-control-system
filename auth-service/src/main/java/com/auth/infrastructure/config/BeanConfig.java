package com.auth.infrastructure.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.auth.ports.in.TokenUseCase;
import com.auth.domain.service.AuthenticateUserService;
import com.auth.domain.service.RegisterUserService;
import com.auth.domain.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth.ports.out.UserRepositoryPort;
import com.auth.ports.out.RoleRepositoryPort;
import com.auth.ports.out.PasswordEncoderPort;
import com.auth.ports.out.TokenProviderPort;

@Configuration
public class BeanConfig {
    
    @Bean
    public AuthenticateUserService authenticateUserService(
        UserRepositoryPort userRepositoryPort,
        PasswordEncoderPort passwordEncoderPort, 
        TokenUseCase tokenUseCase) {
        
        return new AuthenticateUserService(
            userRepositoryPort, passwordEncoderPort, tokenUseCase);
    }

    @Bean
    public RegisterUserService registerUserService(
        RoleRepositoryPort roleRepositoryPort,
        UserRepositoryPort userRepositoryPort,
        PasswordEncoderPort passwordEncoderPort) {
            
        return new RegisterUserService(
            roleRepositoryPort, userRepositoryPort, passwordEncoderPort);
    }

    @Bean
    public TokenService tokenService(TokenProviderPort tokenProviderPort) {
        return new TokenService(tokenProviderPort);
    }

}
