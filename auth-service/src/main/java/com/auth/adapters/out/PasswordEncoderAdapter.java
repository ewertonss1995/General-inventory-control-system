package com.auth.adapters.out;

import com.auth.ports.out.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {
    
    private static final Logger log = LoggerFactory.getLogger(PasswordEncoderAdapter.class);

    private final PasswordEncoder springPasswordEncoder;

    public PasswordEncoderAdapter(PasswordEncoder springPasswordEncoder) {
        this.springPasswordEncoder = springPasswordEncoder;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        log.debug("Gerando hash da senha");
        try {
            return springPasswordEncoder.encode(rawPassword);
        } catch (Exception ex) {
            log.error("Erro inesperado ao gerar hash da senha", ex);
            throw ex;
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        log.debug("Verificando se a senha informada corresponde ao hash cadastrado");
        try {
            return springPasswordEncoder.matches(rawPassword, encodedPassword);
        } catch (Exception ex) {
            log.error("Erro inesperado ao verificar equivalência de hash da senha", ex);
            throw ex;
        }
    }
}
