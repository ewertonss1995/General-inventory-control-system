package com.inventory.control.system.infrastructure.config;

import com.inventory.control.system.domain.service.category.CreateCategoryService;
import com.inventory.control.system.domain.service.category.GetCategoryService;
import com.inventory.control.system.domain.service.category.UpdateCategoryService;
import com.inventory.control.system.domain.service.product.CreateProductService;
import com.inventory.control.system.domain.service.product.GetProductService;
import com.inventory.control.system.domain.service.product.UpdateProductService;
import com.inventory.control.system.domain.service.product.UpdateStockService;
import com.inventory.control.system.ports.in.pruduct.CreateProductUseCase;
import com.inventory.control.system.ports.in.pruduct.GetProductUseCase;
import com.inventory.control.system.ports.in.pruduct.UpdateProductUseCase;
import com.inventory.control.system.ports.in.pruduct.UpdateStockUseCase;
import com.inventory.control.system.ports.in.category.CreateCategoryUseCase;
import com.inventory.control.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.system.ports.in.category.UpdateCategoryUseCase;
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
    public GetProductUseCase findProductUseCase(ProductRepositoryPort productRepositoryPort) {
        return new GetProductService(productRepositoryPort);
    }

    @Bean
    public UpdateStockUseCase updateStockUseCase(ProductRepositoryPort productRepositoryPort) {
        return new UpdateStockService(productRepositoryPort);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepositoryPort productRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        return new UpdateProductService(productRepositoryPort, categoryRepositoryPort);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        return new CreateCategoryService(categoryRepositoryPort);
    }

    @Bean
    public GetCategoryUseCase getCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        return new GetCategoryService(categoryRepositoryPort);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryRepositoryPort categoryRepositoryPort) {
        return new UpdateCategoryService(categoryRepositoryPort);
    }
}
