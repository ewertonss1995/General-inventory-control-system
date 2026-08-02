package com.inventory.control.system.domain.service;

import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.FindProductUseCase;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FindProductService implements FindProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(FindProductService.class);

    private final ProductRepositoryPort productRepositoryPort;

    public FindProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public List<Product> findAll() {
        log.info("Executando caso de uso para listar todos os produtos.");

        List<Product> products = productRepositoryPort.findAll();

        log.info("Consulta de produtos concluída no caso de uso. Total retornado: {}", products.size());
        return products;
    }

    @Override
    public Product findBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            log.warn("Tentativa de busca com SKU nulo ou em branco.");
            throw new IllegalArgumentException("O SKU informado para busca não pode ser nulo ou vazio.");
        }

        String formattedSku = sku.trim().toUpperCase();
        log.info("Executando caso de uso para buscar produto pelo SKU: {}", formattedSku);

        return productRepositoryPort.findBySku(formattedSku)
                .orElseThrow(() -> {
                    log.warn("Falha na busca de produto no caso de uso: SKU '{}' não encontrado.", formattedSku);
                    return new ResourceNotFoundException("Produto não encontrado para o SKU: " + formattedSku);
                });
    }
}
