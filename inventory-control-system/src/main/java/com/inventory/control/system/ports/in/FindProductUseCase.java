package com.inventory.control.system.ports.in;

import com.inventory.control.system.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface FindProductUseCase {
    List<Product> findAll();
    Optional<Product> findBySku(String sku);
}