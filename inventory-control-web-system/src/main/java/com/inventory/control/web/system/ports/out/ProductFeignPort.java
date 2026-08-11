package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;

import java.util.List;

public interface ProductFeignPort {
    Product saveProduct(Product product);
    Product updateProduct(String sku, Product product);
    UpdateStock updateProductStock(String sku, UpdateStockInput input);
    List<Product> findAll();
    Product findBySku(String sku);
}