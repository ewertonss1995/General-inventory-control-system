package com.inventory.control.system.ports.out;

import java.security.interfaces.RSAPublicKey;

public interface JwtPublicKeyPort {
    RSAPublicKey getPublicKey();
}