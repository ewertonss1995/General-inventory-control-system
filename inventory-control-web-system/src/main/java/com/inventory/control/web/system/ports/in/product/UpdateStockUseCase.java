package com.inventory.control.web.system.ports.in.product;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStockInput;

public interface UpdateStockUseCase {
    Product execute(UpdateStockInput input);
}