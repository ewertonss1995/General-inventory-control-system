package com.inventory.control.system.ports.in.product;

import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.UpdateStockInput;

public interface UpdateStockUseCase {
    Product execute(String sku, UpdateStockInput input);
}