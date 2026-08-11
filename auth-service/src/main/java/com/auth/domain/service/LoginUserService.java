package com.auth.domain.service;

import com.auth.domain.exception.BusinessException;
import com.auth.domain.model.User;
import com.auth.domain.model.Login;
import com.auth.ports.in.LoginUserUseCase;
import com.auth.ports.in.TokenUseCase;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.ports.out.PasswordEncoderPort;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUserService implements LoginUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginUserService.class);


    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenUseCase tokenUseCase;

    public LoginUserService(UserRepositoryPort userRepositoryPort,
                                    PasswordEncoderPort passwordEncoderPort, 
                                    TokenUseCase tokenUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenUseCase = tokenUseCase;
    }

    @Override
    public String execute(Login login) {
        log.debug("Iniciando processo de autenticação para: {}", login.getUsernameOrEmail());
    
        User user = userRepositoryPort.findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail())
                .orElseThrow(() -> {
                    log.warn("Tentativa de autenticação com usuário/e-mail inexistente: {}", login.getUsernameOrEmail());
                    return new BusinessException("Credenciais inválidas.");
                });

        if (!user.isActive()) {
            log.warn("Tentativa de login bloqueada: a conta do usuário {} está inativa", login.getUsernameOrEmail());
            throw new BusinessException("Conta de usuário inativa.");
        }

        if (!passwordEncoderPort.matches(login.getPassword(), user.getPassword())) {
            log.warn("Falha de autenticação: senha incorreta para o usuário {}", login.getUsernameOrEmail());
            throw new BusinessException("Credenciais inválidas.");
        }

        String token = tokenUseCase.generateToken(user);
        
        log.info("Usuário {} autenticado com sucesso", login.getUsernameOrEmail());
        return token;
    }
}
