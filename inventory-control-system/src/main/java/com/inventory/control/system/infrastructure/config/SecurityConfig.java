package com.inventory.control.system.infrastructure.config;

import com.inventory.control.system.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite o uso de @PreAuthorize se necessário
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (Swagger, Actuator, Health Checks)
                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 1. GETs liberados para ADMIN, MANAGER e OPERATOR
                        .requestMatchers(HttpMethod.GET, "/v1/products/**", "/v1/categories/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_OPERATOR")

                        // 2. POSTs liberados para ADMIN e MANAGER
                        .requestMatchers(HttpMethod.POST, "/v1/products/**", "/v1/categories/**")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                        // 3. PUTs/PATCHs liberados para ADMIN e MANAGER (mesmo nível de escrita do POST)
                        .requestMatchers(HttpMethod.PUT, "/v1/products/**", "/v1/categories/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/v1/products/**", "/v1/categories/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")

                        // 4. DELETEs liberados exclusivamente para MANAGER
                        .requestMatchers(HttpMethod.DELETE, "/v1/products/**", "/v1/categories/**")
                        .hasAuthority("ROLE_MANAGER")

                        // Qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}