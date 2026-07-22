package com.inventory.control.web.system.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.adapters.out.exception.CustomFeignErrorDecoder;
import com.inventory.control.web.system.infrastructure.security.FeignSecurityInterceptor;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    private final ObjectMapper objectMapper;

    public FeignConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder(objectMapper);
    }

    @Bean
    public RequestInterceptor feignSecurityInterceptor() {
        return new FeignSecurityInterceptor();
    }
}