package com.inventory.control.web.system.domain.service;

import com.inventory.control.web.system.domain.model.ProductItem;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.RegisterProductUseCase;
import com.inventory.control.web.system.ports.out.CreateProductPort;
import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class RegisterProductService implements RegisterProductUseCase {

    private final CreateProductPort createProductPort;

    public RegisterProductService(CreateProductPort createProductPort) {
        this.createProductPort = createProductPort;
    }

    @Override
    public ProductItem execute(ProductRequest productRequest) {
        Product product = new Product(
                productRequest.sku(),
                productRequest.name(),
                productRequest.description(),
                productRequest.price(),
                productRequest.quantity(),
                productRequest.categoryId()
        );
        return createProductPort.createProduct(product);
    }
}
