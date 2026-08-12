package com.auth.infrastructure.security;

import com.auth.ports.out.PasswordEncoderPort;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.auth.adapters.out.exception.PasswordEncryptionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PasswordEncoderProvider implements PasswordEncoderPort {

    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderProvider.class);

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderProvider(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null || rawPassword.length() == 0) {
            log.warn("Tentativa de criptografar uma senha nula ou vazia.");
            throw new IllegalArgumentException("A senha para criptografia não pode ser nula ou vazia.");
        }

        log.debug("Gerando hash da senha.");
        try {
            return passwordEncoder.encode(rawPassword);
        } catch (Exception ex) {
            log.error("Erro ao gerar o hash da senha.", ex);
            throw new PasswordEncryptionException("Erro ao processar a segurança da senha.", ex);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            log.warn("Tentativa de comparação de senha com valores nulos.");
            return false;
        }

        log.debug("Verificando se a senha informada corresponde ao hash cadastrado.");
        try {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception ex) {
            log.error("Erro ao verificar o hash da senha.", ex);
            throw new PasswordEncryptionException("Erro ao validar a credencial informada.", ex);
        }
    }
}
