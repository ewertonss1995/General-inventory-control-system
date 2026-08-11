package com.auth.ports.out;

import com.auth.domain.model.User;
import java.util.Set;

public interface TokenProviderPort {
    String generateToken(User user);
}