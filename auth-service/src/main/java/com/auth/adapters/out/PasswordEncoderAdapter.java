package com.auth.adapters.out;

import com.auth.ports.out.PasswordEncoderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort {

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
