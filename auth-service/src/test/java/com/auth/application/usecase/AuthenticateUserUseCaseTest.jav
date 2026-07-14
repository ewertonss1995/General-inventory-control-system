package com.auth.application.usecase;

import com.auth.application.dto.LoginRequest;
import com.auth.application.dto.TokenResponse;
import com.auth.domain.exception.BusinessException;
import com.auth.domain.service.TokenService;
import com.auth.infrastructure.persistence.jpa.entity.UserEntity;
import com.auth.infrastructure.persistence.jpa.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Test
    @DisplayName("Deve autenticar o operador com sucesso quando credenciais forem válidas")
    void shouldAuthenticateSuccessfully() {
        // Arrange
        LoginRequest request = new LoginRequest("john_doe", "password123");
        UserEntity user = UserEntity.builder()
                .username("john_doe")
                .password("hashed_password")
                .active(true)
                .build();

        when(userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("mocked_jwt_token");

        // Act
        TokenResponse response = authenticateUserUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(7200L, response.expiresInSeconds());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio se o usuário não for encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest("unknown_user", "password");
        when(userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            authenticateUserUseCase.execute(request)
        );

        assertEquals("Credenciais inválidas.", exception.getMessage());
        verifyNoInteractions(passwordEncoder, tokenService);
    }

    @Test
    @DisplayName("Deve lançar erro de negócio se a senha for inválida")
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // Arrange
        LoginRequest request = new LoginRequest("john_doe", "wrong_password");
        UserEntity user = UserEntity.builder()
                .username("john_doe")
                .password("hashed_password")
                .active(true)
                .build();

        when(userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            authenticateUserUseCase.execute(request)
        );

        assertEquals("Credenciais inválidas.", exception.getMessage());
        verifyNoInteractions(tokenService);
    }

    @Test
    @DisplayName("Deve lançar erro de negócio se o operador estiver inativo no banco")
    void shouldThrowExceptionWhenUserIsInactive() {
        // Arrange
        LoginRequest request = new LoginRequest("john_doe", "password123");
        UserEntity user = UserEntity.builder()
                .username("john_doe")
                .password("hashed_password")
                .active(false) // CONTA INATIVA
                .build();

        when(userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail()))
                .thenReturn(Optional.of(user));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            authenticateUserUseCase.execute(request)
        );

        assertEquals("Conta de usuário inativa.", exception.getMessage());
        verifyNoInteractions(passwordEncoder, tokenService);
    }
}