package com.inventory.control.system.ports.out;

import com.inventory.control.system.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    boolean existsBySku(String sku);
    List<Product> findAll();
    Optional<Product> findBySku(String sku);
}