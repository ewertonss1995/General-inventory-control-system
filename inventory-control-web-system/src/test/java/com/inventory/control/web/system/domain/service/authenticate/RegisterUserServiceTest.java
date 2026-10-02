package com.inventory.control.web.system.domain.service.authenticate;

import com.inventory.control.web.system.domain.model.RegisterUser;
import com.inventory.control.web.system.ports.out.AuthenticateFeignPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.inventory.control.web.system.mocks.domain.service.authenticate.AuthDomainMockFactory.createRegisterUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private AuthenticateFeignPort authenticateFeignPort;

    @InjectMocks
    private RegisterUserService registerUserService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterUser registerUser = createRegisterUser();

        assertDoesNotThrow(() -> registerUserService.execute(registerUser));

        verify(authenticateFeignPort).createUser(registerUser);
    }
}