package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.product.GetProductUseCase;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetProductService implements GetProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetProductService.class);

    private final ProductFeignPort productFeignPort;

    public GetProductService(ProductFeignPort productFeignPort) {
        this.productFeignPort = productFeignPort;
    }

    @Override
    public List<Product> findAll() {
        log.info("Executando caso de uso para listar todos os produtos.");

        List<Product> products = productFeignPort.findAll();

        log.info("Consulta de produtos concluída. Total retornado: {}", products.size());
        
        return products;
    }

    @Override
    public Product findBySku(String sku) { 
        String formattedSku = sku != null ? sku.trim().toUpperCase() : null;
        
        log.info("Executando caso de uso para buscar produto pelo SKU: {}", formattedSku);

        Product product = productFeignPort.findBySku(formattedSku);
        
        log.info("Consulta de produto SKU '{}' concluída.", product.getSku());

        return product;
                
    }
}
