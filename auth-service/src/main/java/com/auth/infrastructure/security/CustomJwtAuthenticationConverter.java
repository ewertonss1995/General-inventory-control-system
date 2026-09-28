package com.auth.infrastructure.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");

        Collection<GrantedAuthority> authorities = (roles != null)
                ? roles.stream().map(SimpleGrantedAuthority::new).map(GrantedAuthority.class::cast).toList()
                : List.of();

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}
