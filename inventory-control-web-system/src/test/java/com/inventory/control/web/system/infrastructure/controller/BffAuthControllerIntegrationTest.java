package com.inventory.control.web.system.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.application.dto.LoginDto;
import com.inventory.control.web.system.application.dto.RegisterUserDto;
import com.inventory.control.web.system.application.dto.TokenDto;
import org.junit.jupiter.api.AfterAll; // Alterado para AfterAll
import org.junit.jupiter.api.BeforeAll; // Alterado para BeforeAll
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Ativa o application-test.yml
class BffAuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    private static com.github.tomakehurst.wiremock.WireMockServer wireMockServer;

    // 1. Inicializa o WireMock ANTES de o Spring tentar resolver as propriedades de contexto
    @BeforeAll
    static void startWireMock() {
        wireMockServer = new com.github.tomakehurst.wiremock.WireMockServer(
                com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig().dynamicPort()
        );
        wireMockServer.start();
    }

    // 2. Garante que o cliente estático do WireMock saiba em qual porta o servidor dinâmico subiu antes de cada teste
    @BeforeEach
    void setupWireMockClient() {
        configureFor("localhost", wireMockServer.port());
    }

    // 3. Finaliza o servidor após todos os testes da classe rodarem
    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    // Agora que o WireMock inicia no @BeforeAll, o wireMockServer não estará nulo aqui!
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", () -> wireMockServer.port());
    }

    @Test
    @DisplayName("Deve registrar um operador com sucesso via BFF e retornar 201")
    void shouldRegisterOperatorSuccessfully() throws Exception {
        RegisterUserDto request = new RegisterUserDto("lucas_dev", "lucas@email.com", "senhaSegura123");

        stubFor(post(urlEqualTo("/auth/register"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                .willReturn(aResponse()
                        .withStatus(201)));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/web/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve realizar login e retornar o TokenDto perfeitamente")
    void shouldLoginSuccessfully() throws Exception {
        LoginDto request = new LoginDto("lucas_dev", "senhaSegura123");
        TokenDto expectedResponse = new TokenDto("mocked-jwt-token", "Bearer", 7200L);

        stubFor(post(urlEqualTo("/auth/login"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(expectedResponse))));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/web/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInSeconds").value(7200));
    }

    @Test
    @DisplayName("Deve propagar erro RFC 7807 recebido do Auth-Service")
    void shouldPropagateRfc7807ErrorFromAuthService() throws Exception {
        LoginDto request = new LoginDto("usuario_invalido", "senha_errada");
        
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Usuário ou senha incorretos."
        );
        problemDetail.setTitle("Credenciais Inválidas");
        problemDetail.setType(URI.create("https://api.inventory-control.com/errors/invalid-credentials"));

        stubFor(post(urlEqualTo("/auth/login"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(problemDetail))));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/web/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Credenciais Inválidas"))
                .andExpect(jsonPath("$.detail").value("Usuário ou senha incorretos."))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("https://api.inventory-control.com/errors/invalid-credentials"));
    }
}