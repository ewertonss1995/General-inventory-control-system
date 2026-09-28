package com.inventory.control.web.system.infrastructure.decoder;

import com.inventory.control.web.system.adapters.exception.IntegrationException;
import com.inventory.control.web.system.adapters.in.web.dto.response.ErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

@Component
public class CustomFeignErrorDecoder implements ErrorDecoder {
    private static final Logger log = LoggerFactory.getLogger(CustomFeignErrorDecoder.class);

    private final ObjectMapper objectMapper;

    public CustomFeignErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        String requestUrl = response.request() != null ? response.request().url() : "N/A";

        log.warn("Erro HTTP {} retornado na chamada Feign: [{}] | Método: {}", status, requestUrl, methodKey);

        if (response.body() == null) {
            log.warn("Corpo da resposta de erro do Feign está nulo. Usando exceção fallback para status {}", status);
            return buildFallbackException(status);
        }

        try (InputStream bodyIs = response.body().asInputStream()) {
            ErrorResponse errorResponse = objectMapper.readValue(bodyIs, ErrorResponse.class);
            
            log.info("Sucesso ao desserializar resposta de erro da integração: {}", errorResponse);
            return new IntegrationException(status, errorResponse);

        } catch (IOException e) {
            log.error("Falha ao desserializar o corpo do erro HTTP {} para a classe ErrorResponse. Motivo: {}", 
                    status, e.getMessage());
            return buildFallbackException(status);
        }
    }

    private IntegrationException buildFallbackException(int status) {
        return new IntegrationException(status, "Erro de comunicação remota ao processar a requisição.");
    }
}
