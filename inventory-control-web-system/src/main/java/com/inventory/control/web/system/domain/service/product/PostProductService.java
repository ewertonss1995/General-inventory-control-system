package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import com.inventory.control.web.system.domain.exception.BusinessException;
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
        if (product.getSku() == null || product.getSku().isBlank()) {
            log.warn("Falha ao criar produto: SKU informado é nulo ou vazio.");
            throw new BusinessException("O SKU do produto é obrigatório para criação.");
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            log.warn("Falha ao criar produto SKU '{}': Categoria ou ID da categoria é nulo.", product.getSku());
            throw new BusinessException("É necessário informar uma categoria válida para o produto.");
        }
        
        log.info("Executando caso de uso para criação do produto SKU: {}.", product.getSku());

        Product createdProduct = productFeignPort.saveProduct(product);

        log.info("Produto {} com SKU '{}' criado com sucesso.", createdProduct.getName(), createdProduct.getSku());

        return createdProduct;
    }
}
