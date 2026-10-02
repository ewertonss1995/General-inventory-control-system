package com.auth.adapters.in.web;

import com.auth.adapters.in.web.dto.LoginRequest;
import com.auth.adapters.in.web.dto.RegisterRequest;
import com.auth.domain.model.Login;
import com.auth.domain.model.User;
import com.auth.mocks.AuthMockFactory;
import com.auth.ports.in.LoginUserUseCase;
import com.auth.ports.in.RegisterUserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private LoginUserUseCase loginUserUseCase;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = AuthMockFactory.createRegisterRequest();

        doNothing().when(registerUserUseCase).execute(any(User.class));

        mockMvc.perform(post("/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(registerUserUseCase).execute(any(User.class));
    }

    @Test
    void shouldReturnBadRequestWhenRegisterDataIsInvalid() throws Exception {
        RegisterRequest invalidRequest = AuthMockFactory.createInvalidRegisterRequest();

        mockMvc.perform(post("/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(registerUserUseCase);
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = AuthMockFactory.createLoginRequest();
        String mockJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token.mock";

        when(loginUserUseCase.execute(any(Login.class))).thenReturn(mockJwtToken);

        mockMvc.perform(post("/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(mockJwtToken))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresInSeconds").value(7200));

        verify(loginUserUseCase).execute(any(Login.class));
    }

    @Test
    void shouldReturnBadRequestWhenLoginDataIsInvalid() throws Exception {
        LoginRequest invalidRequest = AuthMockFactory.createInvalidLoginRequest();

        mockMvc.perform(post("/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(loginUserUseCase);
    }
}