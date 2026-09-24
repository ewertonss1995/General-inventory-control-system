package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostProductService implements PostProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(PostProductService.class);

    private final ProductFeignPort productFeignPort;
    
    public PostProductService(ProductFeignPort productFeignPort) {
        this.productFeignPort = productFeignPort;
    }

    @Override
    public Product execute(Product product) {
        log.info("Executando caso de uso para criação do produto SKU: {}.", product.getSku());

        Product createdProduct = productFeignPort.saveProduct(product);

        log.info("Produto {} com SKU '{}' criado com sucesso.", createdProduct.getName(), createdProduct.getSku());

        return createdProduct;
    }
}
