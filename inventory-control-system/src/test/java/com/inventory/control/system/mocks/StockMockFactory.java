package com.inventory.control.system.mocks;

import com.inventory.control.system.domain.model.enums.StockMovementType;
import com.inventory.control.system.domain.model.UpdateStockInput;

public final class StockMockFactory {

    public static final int DEFAULT_MOVEMENT_QUANTITY = 5;

    private StockMockFactory() {
    }

    public static UpdateStockInput createInMovementInput() {
        return new UpdateStockInput(DEFAULT_MOVEMENT_QUANTITY, StockMovementType.IN);
    }

    public static UpdateStockInput createOutMovementInput() {
        return new UpdateStockInput(DEFAULT_MOVEMENT_QUANTITY, StockMovementType.OUT);
    }

    public static UpdateStockInput createCustomMovementInput(StockMovementType stockMovementType, int quantity) {
        return new UpdateStockInput(quantity, stockMovementType);
    }
}