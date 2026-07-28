package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.ProductItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FetchProductPort {
    List<ProductItem> getAllProducts();
    Optional<ProductItem> getProductBySku(String sku);
}