package com.inventory.control.web.system.infrastructure.config;

import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateStockUseCase;
import com.inventory.control.web.system.ports.in.product.GetProductUseCase;
import com.inventory.control.web.system.ports.in.authenticate.LoginUserUseCase;
import com.inventory.control.web.system.ports.in.authenticate.RegisterUserUseCase;
import com.inventory.control.web.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.web.system.ports.in.category.PostCategoryUseCase;
import com.inventory.control.web.system.ports.in.category.UpdateCategoryUseCase;
import com.inventory.control.web.system.domain.service.product.PostProductService;
import com.inventory.control.web.system.domain.service.product.GetProductService;
import com.inventory.control.web.system.domain.service.product.UpdateProductService;
import com.inventory.control.web.system.domain.service.product.UpdateStockService;
import com.inventory.control.web.system.domain.service.authenticate.LoginUserService;
import com.inventory.control.web.system.domain.service.authenticate.RegisterUserService;
import com.inventory.control.web.system.domain.service.category.GetCategoryService;
import com.inventory.control.web.system.domain.service.category.PostCategoryService;
import com.inventory.control.web.system.domain.service.category.UpdateCategoryService;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import com.inventory.control.web.system.ports.out.AuthenticateFeignPort;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class BeanConfig {

    @Bean
    public LoginUserUseCase loginUserUseCase(AuthenticateFeignPort authenticateFeignPort) {
        return new LoginUserService(authenticateFeignPort);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(AuthenticateFeignPort authenticateFeignPort, MeterRegistry meterRegistry) {
        return new RegisterUserService(authenticateFeignPort, meterRegistry);
    }

    @Bean
    public GetProductUseCase getProductUseCase(ProductFeignPort productFeignPort, MeterRegistry meterRegistry) {
        return new GetProductService(productFeignPort, meterRegistry);
    }

    @Bean
    public PostProductUseCase postProductUseCase(ProductFeignPort productFeignPort,
            CategoryFeignPort categoryFeignPort, MeterRegistry meterRegistry) {
        return new PostProductService(productFeignPort, meterRegistry);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductFeignPort productFeignPort,
            CategoryFeignPort categoryFeignPort, MeterRegistry meterRegistry) {
        return new UpdateProductService(productFeignPort, meterRegistry);
    }

    @Bean
    public UpdateStockUseCase updateStockUseCase(ProductFeignPort productFeignPort, MeterRegistry meterRegistry) {
        return new UpdateStockService(productFeignPort, meterRegistry);
    }

    @Bean
    public GetCategoryUseCase getCategoryUseCase(CategoryFeignPort CategoryFeignPort, MeterRegistry meterRegistry) {
        return new GetCategoryService(CategoryFeignPort, meterRegistry);
    }

    @Bean
    public PostCategoryUseCase postCategoryUseCase(CategoryFeignPort categoryFeignPort, MeterRegistry meterRegistry) {
        return new PostCategoryService(categoryFeignPort, meterRegistry);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryFeignPort categoryFeignPort, MeterRegistry meterRegistry) {
        return new UpdateCategoryService(categoryFeignPort, meterRegistry);
    }
}
