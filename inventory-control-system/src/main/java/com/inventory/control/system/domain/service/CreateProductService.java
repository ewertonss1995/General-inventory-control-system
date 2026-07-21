package com.inventory.control.system.domain.service;

import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.CreateProductUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import java.math.BigDecimal;

public class CreateProductService implements CreateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;

    public CreateProductService(ProductRepositoryPort productRepositoryPort, CategoryRepositoryPort categoryRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public Product execute(String sku, String name, String description, BigDecimal price, Integer quantity, Long categoryId) {
        if (productRepositoryPort.existsBySku(sku)) {
            throw new IllegalArgumentException("SKU já cadastrado: " + sku);
        }

        Category category = categoryRepositoryPort.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        Product product = new Product(null, sku, name, description, price, quantity, category);
        return productRepositoryPort.save(product);
    }
}