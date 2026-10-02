package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import com.inventory.control.web.system.domain.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateProductService implements UpdateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProductService.class);

    private final ProductFeignPort productFeignPort;

    public UpdateProductService(ProductFeignPort productFeignPort) {
        this.productFeignPort = productFeignPort;
    }

    @Override
    public Product execute(String sku, Product product) {
        if (sku == null || sku.isBlank()) {
            log.warn("Falha na atualização: SKU informado é nulo ou vazio.");
            throw new BusinessException("O SKU do produto é obrigatório para atualização.");
        }

        if (product.getCategory() == null || product.getCategory().getId() == null) {
            log.warn("Falha na atualização do produto SKU '{}': Categoria ou ID da categoria é nulo.", sku);
            throw new BusinessException("É necessário informar uma categoria válida para atualizar o produto.");
        }    
        
        String formattedSku = sku.trim().toUpperCase();

        log.info("Executando caso de uso para atualização do produto com SKU: {}",formattedSku);

        Product updatedProduct = productFeignPort.updateProduct(sku, product);

        log.info("Produto com SKU '{}' atualizado com sucesso.", updatedProduct.getSku());

        return updatedProduct;
    }
}
