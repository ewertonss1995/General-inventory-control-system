package com.inventory.control.system.ports.in.pruduct;

import com.inventory.control.system.domain.model.Product;
import java.util.List;

public interface FindProductUseCase {
    List<Product> findAll();
    Product findBySku(String sku);
}