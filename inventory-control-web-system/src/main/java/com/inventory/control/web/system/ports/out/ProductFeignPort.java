package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductStockRequest;
import com.inventory.control.web.system.domain.model.Product;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductFeignPort {
    Product saveProduct(Product product);
    Product updateProduct(Product product);
    Map<String, String> updateProductStock(String sku, InventoryProductStockRequest request);
    List<Product> findAll();
    Optional<Product> findBySku(String sku);
}