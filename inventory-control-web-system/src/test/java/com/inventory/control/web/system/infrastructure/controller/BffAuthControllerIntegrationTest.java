package com.inventory.control.web.system.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.application.dto.LoginDto;
import com.inventory.control.web.system.application.dto.RegisterUserDto;
import com.inventory.control.web.system.application.dto.TokenDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BffAuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;
    
    private static com.github.tomakehurst.wiremock.WireMockServer wireMockServer;

    private static final String JWT_SECRET = "eYmIrhhGLEP/xQ/V4AzbcJXMHjGcA29D9QzD0O9oklo=";

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new com.github.tomakehurst.wiremock.WireMockServer(
                com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig().dynamicPort()
        );
        wireMockServer.start();
        // Vincula os métodos estáticos como stubFor à instância dinâmica rodando
        com.github.tomakehurst.wiremock.client.WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.port", () -> wireMockServer.port());
        // Injeta a URL completa com a porta dinâmica sob o nó exato mapeado nas integrações
        registry.add("integrations.auth-service.url", () -> "http://localhost:" + wireMockServer.port());
        registry.add("app.security.jwt-secret", () -> JWT_SECRET);
        registry.add("spring.security.oauth2.resourceserver.jwt.secret-key-spec", () -> JWT_SECRET);
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