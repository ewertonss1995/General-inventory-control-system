package com.inventory.control.web.system.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.adapters.in.web.dto.request.LoginRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.RegisterUserRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.TokenResponse;
import com.inventory.control.web.system.adapters.in.web.mapper.AuthenticateMapper;
import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.domain.model.TokenUser;
import com.inventory.control.web.system.ports.in.authenticate.LoginUserUseCase;
import com.inventory.control.web.system.ports.in.authenticate.RegisterUserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.inventory.control.web.system.adapters.in.web.AuthWebMockFactory.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticateMapper mapper;

    @MockBean
    private LoginUserUseCase loginUserUseCase;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @Nested
    @DisplayName("POST /api/v1/auth/register - Cadastro de Usuário")
    class RegisterTests {

        @Test
        void shouldRegisterUserSuccessfully() throws Exception {
            RegisterUserRequest request = createValidRegisterUserRequest();
            RegisterUser domainModel = createRegisterUserDomain();

            when(mapper.toRegisterUser(request)).thenReturn(domainModel);
            doNothing().when(registerUserUseCase).execute(domainModel);

            mockMvc.perform(post("/api/v1/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            verify(mapper).toRegisterUser(request);
            verify(registerUserUseCase).execute(domainModel);
        }

        @Test
        void shouldReturn400WhenRegisterRequestIsInvalid() throws Exception {
            RegisterUserRequest invalidRequest = createInvalidRegisterUserRequest();

            mockMvc.perform(post("/api/v1/auth/register")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(registerUserUseCase);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/login - Autenticação")
    class LoginTests {

        @Test
        void shouldLoginSuccessfully() throws Exception {
            LoginRequest request = createValidLoginRequest();
            LoginUser loginUserDomain = createLoginUserDomain();
            TokenUser tokenUserDomain = createTokenUserDomain();
            TokenResponse tokenResponse = createTokenResponse();

            when(mapper.toLoginUser(request)).thenReturn(loginUserDomain);
            when(loginUserUseCase.execute(loginUserDomain)).thenReturn(tokenUserDomain);
            when(mapper.toTokenResponse(tokenUserDomain)).thenReturn(tokenResponse);

            mockMvc.perform(post("/api/v1/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value(DEFAULT_TOKEN))
                    .andExpect(jsonPath("$.tokenType").value(DEFAULT_TOKEN_TYPE))
                    .andExpect(jsonPath("$.expiresInSeconds").value(DEFAULT_EXPIRES_IN));

            verify(mapper).toLoginUser(request);
            verify(loginUserUseCase).execute(loginUserDomain);
            verify(mapper).toTokenResponse(tokenUserDomain);
        }

        @Test
        void shouldReturn400WhenLoginRequestIsInvalid() throws Exception {
            LoginRequest invalidRequest = createInvalidLoginRequest();

            mockMvc.perform(post("/api/v1/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(loginUserUseCase);
        }
    }
}