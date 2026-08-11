package com.auth.infrastructure.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private Environment environment;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean isDevProfile = Arrays.asList(environment.getActiveProfiles()).contains("dev");

        return http
            .csrf(csrf -> csrf.disable()) // Desabilita CSRF
            .authorizeHttpRequests(authorize -> {
                // ✅ Executa toH2Console() APENAS se o perfil ativo for "dev"
                if (isDevProfile) {
                    authorize.requestMatchers(PathRequest.toH2Console()).permitAll();
                }

                authorize
                    .requestMatchers("/auth/login", "/auth/register").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .anyRequest().authenticated();
            })
            .headers(headers -> {
                // ✅ Permite frames do H2 apenas em ambiente de dev
                if (isDevProfile) {
                    headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
                }
            })
            .build();
    }
}
