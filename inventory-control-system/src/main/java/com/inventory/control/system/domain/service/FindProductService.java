package com.inventory.control.system.domain.service;

import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.CreateProductUseCase;
import com.inventory.control.system.ports.in.FindProductUseCase; // <-- Nova Port importada
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import com.inventory.control.system.ports.out.ProductRepositoryPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class FindProductService implements FindProductUseCase {
    
    private final ProductRepositoryPort productRepositoryPort;
    
    public FindProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public List<Product> findAll() {
        return productRepositoryPort.findAll();
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        if (sku == null || sku.isBlank()) {
            return Optional.empty();
        }

        return productRepositoryPort.findBySku(sku.trim().toUpperCase());
    }
}
