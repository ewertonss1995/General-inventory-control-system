package com.inventory.control.web.system.ports.in;

import com.inventory.control.web.system.domain.model.ProductItem;
import java.util.List;
import java.util.Optional;

public interface FetchCatalogUseCase {
    List<ProductItem> fetchAll();
    Optional<ProductItem> fetchBySku(String sku);
}