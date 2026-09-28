package com.auth.domain.service;

import com.auth.domain.exception.BusinessException;
import com.auth.domain.model.User;
import com.auth.domain.model.Login;
import com.auth.ports.in.LoginUserUseCase;
import com.auth.ports.in.TokenUseCase;
import com.auth.ports.out.UserRepositoryPort;
import com.auth.ports.out.PasswordEncoderPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUserService implements LoginUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(LoginUserService.class);

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenUseCase tokenUseCase;
    private final MeterRegistry meterRegistry;

    public LoginUserService(UserRepositoryPort userRepositoryPort,
                            PasswordEncoderPort passwordEncoderPort, 
                            TokenUseCase tokenUseCase,
                            MeterRegistry meterRegistry) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenUseCase = tokenUseCase;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public String execute(Login login) {
        return Timer.builder("usecase.auth.login.time")
            .description("Tempo total de execução do caso de uso de autenticação (login)")
            .tag("layer", "usecase")
            .register(meterRegistry)
            .record(() -> {
                log.debug("Iniciando processo de autenticação para: {}", login.getUsernameOrEmail());

                User user = userRepositoryPort.findByUsernameOrEmail(login.getUsernameOrEmail(), login.getUsernameOrEmail())
                        .orElseThrow(() -> {
                            log.warn("Tentativa de autenticação com usuário/e-mail inexistente: {}", login.getUsernameOrEmail());
                            recordFailure("user_not_found");
                            return new BusinessException("Credenciais inválidas.");
                        });

                if (!user.isActive()) {
                    log.warn("Tentativa de login bloqueada: a conta do usuário {} está inativa", login.getUsernameOrEmail());
                    recordFailure("account_inactive");
                    throw new BusinessException("Conta de usuário inativa.");
                }

                if (!passwordEncoderPort.matches(login.getPassword(), user.getPassword())) {
                    log.warn("Falha de autenticação: senha incorreta para o usuário {}", login.getUsernameOrEmail());
                    recordFailure("invalid_password");
                    throw new BusinessException("Credenciais inválidas.");
                }
                
                String token = tokenUseCase.generateToken(user);

                meterRegistry.counter("business.auth.login.success", "layer", "usecase").increment();
                log.info("Usuário {} autenticado com sucesso", login.getUsernameOrEmail());

                return token;
                });
            }

private void recordFailure(String reason) {
    meterRegistry.counter("business.auth.login.failures",
            "layer", "usecase",
            "reason", reason).increment();
    }
}
