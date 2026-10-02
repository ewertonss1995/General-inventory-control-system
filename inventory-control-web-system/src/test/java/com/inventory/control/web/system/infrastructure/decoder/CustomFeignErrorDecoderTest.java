package com.inventory.control.web.system.infrastructure.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.adapters.exception.IntegrationException;
import com.inventory.control.web.system.adapters.in.web.dto.response.ErrorResponse;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomFeignErrorDecoderTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CustomFeignErrorDecoder customFeignErrorDecoder;

    private Request request;

    @BeforeEach
    void setUp() {
        request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost:8080/api/v1/resource",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }

    @Test
    void shouldDecodeErrorResponseSuccessfullyWhenBodyIsValidJson() {
        int httpStatus = 400;
        String methodKey = "ProductFeignPort#findBySku(String)";
        String jsonBody = "{\"message\":\"Produto não encontrado\",\"status\":400}";

        Response response = Response.builder()
                .status(httpStatus)
                .reason("Bad Request")
                .request(request)
                .body(jsonBody, StandardCharsets.UTF_8)
                .build();

        Exception exception = customFeignErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof IntegrationException);

        IntegrationException integrationException = (IntegrationException) exception;
        assertEquals(httpStatus, integrationException.getStatus());
        assertNotNull(integrationException.getErrorResponse());
        assertEquals("Produto não encontrado", integrationException.getErrorResponse().message());
    }

    @Test
    void shouldReturnFallbackExceptionWhenBodyIsNull() {
        int httpStatus = 500;
        String methodKey = "ProductFeignPort#saveProduct(Product)";

        Response response = Response.builder()
                .status(httpStatus)
                .reason("Internal Server Error")
                .request(request)
                .body((Response.Body) null)
                .build();

        Exception exception = customFeignErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof IntegrationException);

        IntegrationException integrationException = (IntegrationException) exception;
        assertEquals(httpStatus, integrationException.getStatus());
        assertEquals("Erro de comunicação remota ao processar a requisição.", integrationException.getMessage());
    }

    @Test
    void shouldReturnFallbackExceptionWhenJsonIsMalformed() {
        int httpStatus = 502;
        String methodKey = "CategoryFeignPort#findAll()";
        String malformedJson = "invalid json body {";

        Response response = Response.builder()
                .status(httpStatus)
                .reason("Bad Gateway")
                .request(request)
                .body(malformedJson, StandardCharsets.UTF_8)
                .build();

        Exception exception = customFeignErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof IntegrationException);

        IntegrationException integrationException = (IntegrationException) exception;
        assertEquals(httpStatus, integrationException.getStatus());
        assertEquals("Erro de comunicação remota ao processar a requisição.", integrationException.getMessage());
    }

    @Test
    void shouldReturnFallbackExceptionWhenIoExceptionOccursOnRead() throws Exception {
        int httpStatus = 500;
        String methodKey = "AuthenticateFeignPort#userLogin(LoginUser)";

        Response response = Response.builder()
                .status(httpStatus)
                .reason("Internal Server Error")
                .request(request)
                .body("{}", StandardCharsets.UTF_8)
                .build();

        doThrow(new IOException("Simulated IO Error"))
                .when(objectMapper).readValue(any(InputStream.class), eq(ErrorResponse.class));

        Exception exception = customFeignErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof IntegrationException);

        IntegrationException integrationException = (IntegrationException) exception;
        assertEquals(httpStatus, integrationException.getStatus());
        assertEquals("Erro de comunicação remota ao processar a requisição.", integrationException.getMessage());
    }

    @Test
    void shouldHandleDecodeGracefullyWhenRequestIsNull() {
        int httpStatus = 404;
        String methodKey = "ProductFeignPort#findBySku(String)";

        Response response = mock(Response.class);
        when(response.status()).thenReturn(httpStatus);
        when(response.request()).thenReturn(null);
        when(response.body()).thenReturn(null);

        Exception exception = customFeignErrorDecoder.decode(methodKey, response);

        assertNotNull(exception);
        assertTrue(exception instanceof IntegrationException);

        IntegrationException integrationException = (IntegrationException) exception;
        assertEquals(httpStatus, integrationException.getStatus());
    }
}