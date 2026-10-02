package com.inventory.control.web.system.domain.service.authenticate;

import com.inventory.control.web.system.domain.model.LoginUser;
import com.inventory.control.web.system.domain.model.TokenUser;
import com.inventory.control.web.system.ports.out.AuthenticateFeignPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.inventory.control.web.system.mocks.domain.service.authenticate.AuthDomainMockFactory.createLoginUser;
import static com.inventory.control.web.system.mocks.domain.service.authenticate.AuthDomainMockFactory.createTokenUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUserServiceTest {

    @Mock
    private AuthenticateFeignPort authenticateFeignPort;

    @InjectMocks
    private LoginUserService loginUserService;

    @Test
    void shouldAuthenticateUserSuccessfully() {
        LoginUser loginUser = createLoginUser();
        TokenUser expectedTokenUser = createTokenUser();

        when(authenticateFeignPort.userLogin(loginUser)).thenReturn(expectedTokenUser);

        TokenUser result = loginUserService.execute(loginUser);

        assertNotNull(result);
        assertEquals(expectedTokenUser.getAccessToken(), result.getAccessToken());
        assertEquals(expectedTokenUser.getTokenType(), result.getTokenType());
        assertEquals(expectedTokenUser.getExpiresInSeconds(), result.getExpiresInSeconds());

        verify(authenticateFeignPort).userLogin(loginUser);
    }
}