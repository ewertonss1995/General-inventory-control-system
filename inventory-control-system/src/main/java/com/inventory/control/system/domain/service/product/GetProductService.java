package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.product.GetProductUseCase;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetProductService implements GetProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetProductService.class);

    private final ProductRepositoryPort productRepositoryPort;

    public GetProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

@Override
    public List<Product> findAll() {
        log.info("Executando caso de uso para listar todos os produtos.");

        List<Product> products = productRepositoryPort.findAll();

        log.info("Consulta de produtos concluída. Total retornado: {}", products.size());
        return products;
    }

    @Override
    public Product findBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            log.warn("Tentativa de busca com SKU nulo ou em branco. SKU recebido: '{}'", sku);
            throw new BusinessException("O SKU informado para busca não pode ser nulo ou vazio.");
        }

        String formattedSku = sku.trim().toUpperCase();
        log.info("Executando caso de uso para buscar produto pelo SKU: {}", formattedSku);

        return productRepositoryPort.findBySku(formattedSku)
                .orElseThrow(() -> {
                    log.warn("Falha na busca de produto: SKU '{}' não encontrado.", formattedSku);
                    return new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
                });
    }
}
