package com.inventory.control.web.system.ports.out;

import java.security.interfaces.RSAPublicKey;

public interface JwtPublicKeyPort {
    RSAPublicKey getPublicKey();
}