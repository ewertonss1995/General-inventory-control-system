package com.inventory.control.web.system.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.infrastructure.decoder.CustomFeignErrorDecoder;
import com.inventory.control.web.system.infrastructure.interceptor.FeignSecurityInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
    public FeignSecurityInterceptor feignSecurityInterceptor() {
        return new FeignSecurityInterceptor();
    }
}
