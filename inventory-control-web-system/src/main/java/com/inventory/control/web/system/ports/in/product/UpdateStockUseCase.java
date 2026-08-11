package com.inventory.control.web.system.ports.in.product;

import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;

public interface UpdateStockUseCase {
    UpdateStock execute(String sku, UpdateStockInput input);
}