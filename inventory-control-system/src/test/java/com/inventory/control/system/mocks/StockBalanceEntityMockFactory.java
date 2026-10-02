package com.inventory.control.system.mocks;

import java.util.UUID;
import com.inventory.control.system.adapters.out.database.postgres.entities.StockBalanceEntity;

public final class StockBalanceEntityMockFactory {

    public static final UUID DEFAULT_STOCK_ID = UUID.randomUUID();
    public static final String DEFAULT_PRODUCT_ID = ProductMockFactory.DEFAULT_PRODUCT_ID;
    public static final String DEFAULT_SKU = ProductMockFactory.DEFAULT_SKU;
    public static final Integer DEFAULT_QUANTITY = ProductMockFactory.DEFAULT_QUANTITY;

    private StockBalanceEntityMockFactory() {
    }

    public static StockBalanceEntity createStockBalanceEntity() {
        return createStockBalanceEntity(DEFAULT_STOCK_ID, DEFAULT_PRODUCT_ID, DEFAULT_SKU, DEFAULT_QUANTITY);
    }

    public static StockBalanceEntity createStockBalanceEntity(UUID id, String productId, String sku, Integer quantity) {
        return StockBalanceEntity.builder()
                .id(id)
                .productId(productId)
                .sku(sku)
                .quantity(quantity)
                .build();
    }
}