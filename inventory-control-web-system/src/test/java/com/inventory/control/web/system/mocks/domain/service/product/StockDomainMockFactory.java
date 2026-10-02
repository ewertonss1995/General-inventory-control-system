package com.inventory.control.web.system.mocks.domain.service.product;

import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;

public final class StockDomainMockFactory {

    public static final String DEFAULT_SKU = "SKU-PROD-001";
    public static final String UNFORMATTED_SKU = "  sku-prod-001  ";
    public static final Integer DEFAULT_QUANTITY = 10;
    public static final Integer PREVIOUS_QUANTITY = 20;
    public static final Integer NEW_QUANTITY = 30;
    public static final String MOVEMENT_TYPE_ENTRY = "ENTRY";
    public static final String SUCCESS_MESSAGE = "Estoque atualizado com sucesso.";

    private StockDomainMockFactory() {
    }

    public static UpdateStockInput createUpdateStockInput() {
        return new UpdateStockInput(DEFAULT_QUANTITY, MOVEMENT_TYPE_ENTRY);
    }

    public static UpdateStock createUpdateStock() {
        return new UpdateStock(
                DEFAULT_SKU,
                PREVIOUS_QUANTITY,
                NEW_QUANTITY,
                MOVEMENT_TYPE_ENTRY,
                SUCCESS_MESSAGE
        );
    }
}