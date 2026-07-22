package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.ProductItem;
import com.inventory.control.web.system.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InventoryClientPort {
    ProductItem createProduct(Product product);
    List<ProductItem> getAllProducts();
    Optional<ProductItem> getProductBySku(String sku);
}