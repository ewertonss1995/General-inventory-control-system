package com.inventory.control.web.system.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.inventory.control.web.system.infrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                        .requestMatchers("/api/v1/auth/**", "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/categories/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_OPERATOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**", "/api/v1/categories/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**", "/api/v1/categories/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/products/*/stock").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**", "/api/v1/categories/**").hasAuthority("ROLE_MANAGER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
