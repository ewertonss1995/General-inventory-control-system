package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductFeignPort {
    Product saveProduct(Product product);
    Product updateProduct(Product product);
    boolean existsBySku(String sku);
    List<Product> findAll();
    Optional<Product> findBySku(String sku);
}