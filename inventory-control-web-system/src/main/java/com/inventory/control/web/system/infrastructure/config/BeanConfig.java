package com.inventory.control.web.system.infrastructure.config;

import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateStockUseCase;
import com.inventory.control.web.system.ports.in.product.GetProductUseCase;
import com.inventory.control.web.system.ports.in.authenticate.LoginUserUseCase;
import com.inventory.control.web.system.ports.in.authenticate.RegisterUserUseCase;

import com.inventory.control.web.system.domain.service.product.PostProductService;
import com.inventory.control.web.system.domain.service.product.GetProductService;
import com.inventory.control.web.system.domain.service.product.UpdateProductService;
import com.inventory.control.web.system.domain.service.product.UpdateStockService;
import com.inventory.control.web.system.domain.service.authenticate.LoginUserService;
import com.inventory.control.web.system.domain.service.authenticate.RegisterUserService;

import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import com.inventory.control.web.system.ports.out.AuthenticateFeignPort;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public LoginUserUseCase loginUserUseCase(AuthenticateFeignPort authenticateFeignPort) {
        return new LoginUserService(authenticateFeignPort);
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(AuthenticateFeignPort authenticateFeignPort) {
        return new RegisterUserService(authenticateFeignPort);
    }


    @Bean
    public GetProductUseCase getProductUseCase(ProductFeignPort productFeignPort) {
        return new GetProductService(productFeignPort);
    }

    @Bean
    public PostProductUseCase postProductUseCase(ProductFeignPort productFeignPort, CategoryFeignPort categoryFeignPort) {
        return new PostProductService(productFeignPort);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductFeignPort productFeignPort, CategoryFeignPort categoryFeignPort) {
        return new UpdateProductService(productFeignPort);
    }

    @Bean
    public UpdateStockUseCase updateStockUseCase(ProductFeignPort productFeignPort) {
        return new UpdateStockService(productFeignPort);
    }

}
