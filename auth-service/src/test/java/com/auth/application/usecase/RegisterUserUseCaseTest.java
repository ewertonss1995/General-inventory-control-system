package com.auth.application.usecase;

import com.auth.application.dto.RegisterRequest;
import com.auth.domain.exception.BusinessException;
import com.auth.infrastructure.persistence.jpa.entity.RoleEntity;
import com.auth.infrastructure.persistence.jpa.entity.UserEntity;
import com.auth.infrastructure.persistence.jpa.repository.RoleRepository;
import com.auth.infrastructure.persistence.jpa.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @Test
    @DisplayName("Deve registrar um usuário com sucesso quando os dados forem válidos")
    void shouldRegisterUserSuccessfully() {
        // Arrange (Preparação de cenário)
        RegisterRequest request = new RegisterRequest("john_doe", "john@example.com", "mysecret123");
        RoleEntity operatorRole = RoleEntity.builder().name("ROLE_OPERATOR").build();

        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(roleRepository.findByName("ROLE_OPERATOR")).thenReturn(Optional.of(operatorRole));
        when(passwordEncoder.encode(request.password())).thenReturn("encrypted_password");

        // Act (Ação de teste)
        assertDoesNotThrow(() -> registerUserUseCase.execute(request));

        // Assert (Verificações e asserções)
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        UserEntity savedUser = userCaptor.getValue();
        assertEquals("john_doe", savedUser.getUsername());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("encrypted_password", savedUser.getPassword());
        assertTrue(savedUser.isActive());
        assertTrue(savedUser.getRoles().contains(operatorRole));
    }

    @Test
    @DisplayName("Deve lançar erro de negócio se o nome de usuário já estiver em uso")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("existing_user", "new@example.com", "password123");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            registerUserUseCase.execute(request)
        );

        assertEquals("Nome de usuário já está em uso.", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Deve lançar erro de negócio se o e-mail do usuário já estiver em uso")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("new_user", "existing@example.com", "password123");
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            registerUserUseCase.execute(request)
        );

        assertEquals("E-mail já cadastrado.", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}