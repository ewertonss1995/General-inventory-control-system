package com.inventory.control.system.infrastructure.config;

import com.inventory.control.system.domain.service.CreateProductService;
import com.inventory.control.system.domain.service.FindProductService;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CreateProductService createProductService(ProductRepositoryPort productRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        return new CreateProductService(productRepositoryPort, categoryRepositoryPort);
    }

    @Bean
    public FindProductService findProductService(ProductRepositoryPort productRepositoryPort) {
        return new FindProductService(productRepositoryPort);
    }
}