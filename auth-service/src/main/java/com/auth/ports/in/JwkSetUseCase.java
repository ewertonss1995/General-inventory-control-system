package com.auth.ports.in;

import java.util.Map;

public interface JwkSetUseCase {
    Map<String, Object> getKeys();
}