package com.inventory.control.system.ports.in.pruduct;

import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.UpdateStockInput;

public interface UpdateStockUseCase {
    Product execute(UpdateStockInput input);
}