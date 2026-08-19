package com.inventory.control.system.infrastructure.config;

import com.inventory.control.system.ports.out.JwtPublicKeyPort;
import com.inventory.control.system.infrastructure.security.CustomJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtPublicKeyPort jwtPublicKeyPort;

    public SecurityConfig(JwtPublicKeyPort jwtPublicKeyPort) {
        this.jwtPublicKeyPort = jwtPublicKeyPort;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/h2-console/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/products/**", "/v1/categories/**")
                            .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_OPERATOR")
                        .requestMatchers(HttpMethod.POST, "/v1/products/**", "/v1/categories/**")
                            .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/v1/products/**", "/v1/categories/**")
                            .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/v1/products/*/stock")
                            .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/v1/products/**", "/v1/categories/**")
                            .hasAuthority("ROLE_MANAGER")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(jwtPublicKeyPort.getPublicKey()).build();
    }
}
