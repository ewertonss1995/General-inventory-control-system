package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.adapters.in.web.dto.LoginDto;
import com.inventory.control.web.system.adapters.in.web.dto.TokenDto;
import java.util.Map;

public interface AuthClientPort {
    TokenDto authenticate(LoginDto loginDto);
    boolean validateToken(String token);
}