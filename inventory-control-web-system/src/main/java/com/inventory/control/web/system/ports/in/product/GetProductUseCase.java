package com.inventory.control.web.system.ports.in.product;

import com.inventory.control.web.system.domain.model.Product;
import java.util.List;

public interface GetProductUseCase {
    List<Product> findAll();
    Product findBySku(String sku);
}