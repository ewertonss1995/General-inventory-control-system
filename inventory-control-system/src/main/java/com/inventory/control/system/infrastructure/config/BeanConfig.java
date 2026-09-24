package com.inventory.control.system.infrastructure.config;

import com.inventory.control.system.domain.service.category.CreateCategoryService;
import com.inventory.control.system.domain.service.category.GetCategoryService;
import com.inventory.control.system.domain.service.category.UpdateCategoryService;
import com.inventory.control.system.domain.service.product.CreateProductService;
import com.inventory.control.system.domain.service.product.GetProductService;
import com.inventory.control.system.domain.service.product.UpdateProductService;
import com.inventory.control.system.domain.service.product.UpdateStockService;
import com.inventory.control.system.ports.in.category.CreateCategoryUseCase;
import com.inventory.control.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.system.ports.in.category.UpdateCategoryUseCase;
import com.inventory.control.system.ports.in.product.CreateProductUseCase;
import com.inventory.control.system.ports.in.product.GetProductUseCase;
import com.inventory.control.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.system.ports.in.product.UpdateStockUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepositoryPort productRepositoryPort, 
                                                     CategoryRepositoryPort categoryRepositoryPort, 
                                                     MeterRegistry meterRegistry) {
        return new CreateProductService(productRepositoryPort, categoryRepositoryPort, meterRegistry);
    }

    @Bean
    public GetProductUseCase findProductUseCase(ProductRepositoryPort productRepositoryPort, MeterRegistry meterRegistry) {
        return new GetProductService(productRepositoryPort, meterRegistry);
    }

    @Bean
    public UpdateStockUseCase updateStockUseCase(ProductRepositoryPort productRepositoryPort, MeterRegistry meterRegistry) {
        return new UpdateStockService(productRepositoryPort, meterRegistry);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepositoryPort productRepositoryPort, 
                                                     CategoryRepositoryPort categoryRepositoryPort, 
                                                     MeterRegistry meterRegistry) {
        return new UpdateProductService(productRepositoryPort, categoryRepositoryPort, meterRegistry);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort, MeterRegistry meterRegistry) {
        return new CreateCategoryService(categoryRepositoryPort, meterRegistry);
    }

    @Bean
    public GetCategoryUseCase getCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort, MeterRegistry meterRegistry) {
        return new GetCategoryService(categoryRepositoryPort, meterRegistry);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort, MeterRegistry meterRegistry) {
        return new UpdateCategoryService(categoryRepositoryPort, meterRegistry);
    }
}
