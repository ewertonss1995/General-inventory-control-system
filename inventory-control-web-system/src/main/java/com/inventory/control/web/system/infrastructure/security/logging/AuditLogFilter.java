package com.inventory.control.web.system.infrastructure.security.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class AuditLogFilter extends OncePerRequestFilter {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");
    private static final String TRACE_ID_KEY = "traceId";
    private static final String USERNAME_KEY = "auditUser";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // 1. Gera ou recupera um Trace ID único para rastreabilidade distribuída
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        
        // Coloca o Trace ID no cabeçalho da resposta para que o frontend saiba rastrear em caso de erro
        response.addHeader("X-Trace-Id", traceId);

        // 2. Injeta as chaves no MDC (Mapped Diagnostic Context) do Logback para que apareçam no JSON de saída
        MDC.put(TRACE_ID_KEY, traceId);
        
        // Captura o usuário autenticado caso já esteja preenchido pelo Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "ANONYMOUS";
        MDC.put(USERNAME_KEY, username);

        try {
            // Segue o fluxo normal da requisição
            filterChain.doFilter(request, response);
        } finally {
            // 3. Ao retornar a resposta, calcula o tempo de execução e gera o log de auditoria
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();

            // Mensagem padronizada de auditoria
            String auditMessage = String.format("HTTP %s %s | Status: %d | Executed in %dms", method, uri, status, duration);

            // Adiciona campos dinâmicos adicionais de forma contextualizada
            MDC.put("httpMethod", method);
            MDC.put("httpUri", uri);
            MDC.put("httpStatus", String.valueOf(status));
            MDC.put("executionTimeMs", String.valueOf(duration));

            if (status >= 400) {
                auditLogger.warn(auditMessage);
            } else {
                auditLogger.info(auditMessage);
            }

            // 4. Limpa o contexto do thread atual para evitar vazamento de memória (Memory Leak)
            MDC.clear();
        }
    }
}