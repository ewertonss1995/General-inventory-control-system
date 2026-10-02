package com.inventory.control.web.system.infrastructure.interceptor;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeignSecurityInterceptorTest {

    @InjectMocks
    private FeignSecurityInterceptor feignSecurityInterceptor;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    private RequestTemplate requestTemplate;

    @BeforeEach
    void setUp() {
        requestTemplate = new RequestTemplate();
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAddAuthorizationAndTraceIdHeadersWhenBothArePresent() {
        String expectedTraceId = "123456789-abc";
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.dummyToken";
        
        MDC.put("traceId", expectedTraceId);

        when(jwt.getTokenValue()).thenReturn(expectedToken);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        feignSecurityInterceptor.apply(requestTemplate);

        Map<String, Collection<String>> headers = requestTemplate.headers();

        assertTrue(headers.containsKey("X-Trace-Id"));
        assertEquals(expectedTraceId, headers.get("X-Trace-Id").iterator().next());

        assertTrue(headers.containsKey("Authorization"));
        assertEquals("Bearer " + expectedToken, headers.get("Authorization").iterator().next());
    }

    @Test
    void shouldAddOnlyTraceIdHeaderWhenSecurityContextIsEmpty() {
        String expectedTraceId = "trace-id-only-999";
        MDC.put("traceId", expectedTraceId);

        feignSecurityInterceptor.apply(requestTemplate);

        Map<String, Collection<String>> headers = requestTemplate.headers();

        assertTrue(headers.containsKey("X-Trace-Id"));
        assertEquals(expectedTraceId, headers.get("X-Trace-Id").iterator().next());
        assertFalse(headers.containsKey("Authorization"));
    }

    @Test
    void shouldAddOnlyAuthorizationHeaderWhenMdcIsEmpty() {
        String expectedToken = "token-without-trace";

        when(jwt.getTokenValue()).thenReturn(expectedToken);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        feignSecurityInterceptor.apply(requestTemplate);

        Map<String, Collection<String>> headers = requestTemplate.headers();

        assertFalse(headers.containsKey("X-Trace-Id"));
        assertTrue(headers.containsKey("Authorization"));
        assertEquals("Bearer " + expectedToken, headers.get("Authorization").iterator().next());
    }

    @Test
    void shouldNotAddAuthorizationHeaderWhenPrincipalIsNotJwt() {
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        feignSecurityInterceptor.apply(requestTemplate);

        Map<String, Collection<String>> headers = requestTemplate.headers();

        assertFalse(headers.containsKey("Authorization"));
    }

    @Test
    void shouldNotAddAnyHeaderWhenMdcIsEmptyAndSecurityContextIsNull() {
        feignSecurityInterceptor.apply(requestTemplate);

        Map<String, Collection<String>> headers = requestTemplate.headers();

        assertTrue(headers.isEmpty());
    }
}