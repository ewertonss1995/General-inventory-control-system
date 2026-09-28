package com.inventory.control.system.mocks;

import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;

import java.math.BigDecimal;

import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithId;

public final class ProductMockFactory {

    public static final String DEFAULT_PRODUCT_ID = "66b4f1a2e3b0c44298fc1d99";
    public static final String DEFAULT_SKU = "MON-LG-29ULTRA";
    public static final String DEFAULT_LOWERCASE_SKU = "mon-lg-29ultra";
    public static final String DEFAULT_PRODUCT_NAME = "Monitor Ultrawide LG 29";
    public static final String DEFAULT_PRODUCT_DESCRIPTION = "Monitor IPS 29 polegadas Full HD";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("1299.90");
    public static final Integer DEFAULT_QUANTITY = 15;

    private ProductMockFactory() {
    }

    public static Product createProductWithId() {
        return createProduct(DEFAULT_PRODUCT_ID, DEFAULT_SKU, createCategoryWithId());
    }

    public static Product createProductWithoutId() {
        return createProduct(null, DEFAULT_SKU, createCategoryWithId());
    }

    public static Product createProduct(String id, String sku, Category category) {
        return new Product(
                id,
                sku,
                DEFAULT_PRODUCT_NAME,
                DEFAULT_PRODUCT_DESCRIPTION,
                DEFAULT_PRICE,
                DEFAULT_QUANTITY,
                category
        );
    }
}