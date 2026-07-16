package com.inventory.control.web.system.infrastructure.integration.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.domain.exception.IntegrationException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Component
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public CustomFeignErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.body() != null) {
            try (InputStream bodyIs = response.body().asInputStream()) {
                // Tenta ler e converter o erro para o formato RFC 7807 padrão
                ProblemDetail problemDetail = objectMapper.readValue(bodyIs, ProblemDetail.class);
                return new IntegrationException(response.status(), problemDetail);
            } catch (IOException e) {
                // Se falhar ao serializar o corpo do JSON (ex: resposta não mapeada), cai no fallback abaixo
            }
        }

        // Caso o backend não retorne um ProblemDetail válido, montamos um genérico baseado no status recebido
        HttpStatus status = HttpStatus.resolve(response.status());
        ProblemDetail fallbackDetail = ProblemDetail.forStatusAndDetail(
                status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro de comunicação remota ao processar a requisição."
        );
        fallbackDetail.setTitle("Erro de Integração");
        fallbackDetail.setType(URI.create("https://api.inventory-control.com/errors/integration-error"));

        return new IntegrationException(response.status(), fallbackDetail);
    }
}