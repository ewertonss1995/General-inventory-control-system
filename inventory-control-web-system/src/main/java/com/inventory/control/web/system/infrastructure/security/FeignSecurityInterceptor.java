package com.inventory.control.web.system.infrastructure.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class FeignSecurityInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public void apply(RequestTemplate template) {
        String activeTraceId = MDC.get(TRACE_ID_KEY);
        
        if (activeTraceId != null && !activeTraceId.isBlank()) {
            template.header(TRACE_ID_HEADER, activeTraceId);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getCredentials() instanceof Jwt jwt) {
            String tokenValue = jwt.getTokenValue();
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, tokenValue));
        }
    }
}