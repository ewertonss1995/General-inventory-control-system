package com.inventory.control.system.ports.in;

import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.UpdateStockInput;

public interface UpdateStockUseCase {
    Product execute(UpdateStockInput input);
}