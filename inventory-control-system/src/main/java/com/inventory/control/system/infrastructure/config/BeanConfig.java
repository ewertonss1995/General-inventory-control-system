package com.inventory.control.system.infrastructure.config;

import com.inventory.control.system.domain.service.CreateProductService;
import com.inventory.control.system.domain.service.FindProductService;
import com.inventory.control.system.domain.service.UpdateStockService;
import com.inventory.control.system.domain.service.UpdateProductService;
import com.inventory.control.system.ports.in.CreateProductUseCase;
import com.inventory.control.system.ports.in.FindProductUseCase;
import com.inventory.control.system.ports.in.UpdateStockUseCase;
import com.inventory.control.system.ports.in.UpdateProductUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepositoryPort productRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        return new CreateProductService(productRepositoryPort, categoryRepositoryPort);
    }

    @Bean
    public FindProductUseCase findProductUseCase(ProductRepositoryPort productRepositoryPort) {
        return new FindProductService(productRepositoryPort);
    }

    @Bean
    public UpdateStockUseCase updateStockUseCase(ProductRepositoryPort productRepositoryPort) {
        return new UpdateStockService(productRepositoryPort);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepositoryPort productRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        return new UpdateProductService(productRepositoryPort, categoryRepositoryPort);
    }
}