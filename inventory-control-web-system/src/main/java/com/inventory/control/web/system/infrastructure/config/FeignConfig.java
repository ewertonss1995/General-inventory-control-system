package com.inventory.control.web.system.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.adapters.exception.CustomFeignErrorDecoder;

import feign.Client;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }

    @Bean
    public Client feignClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .useSystemProperties()
                .build();
        return new ApacheHttpClient(httpClient);
    }
}
