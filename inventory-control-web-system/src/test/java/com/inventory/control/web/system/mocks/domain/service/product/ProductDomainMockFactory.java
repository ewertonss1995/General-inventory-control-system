package com.inventory.control.web.system.mocks.domain.service.product;

import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;

import java.math.BigDecimal;

public final class ProductDomainMockFactory {

    public static final String DEFAULT_ID = "prod-123";
    public static final String DEFAULT_SKU = "SKU-PROD-001";
    public static final String DEFAULT_NAME = "Monitor Gamer 27";
    public static final String DEFAULT_DESCRIPTION = "Monitor IPS 144Hz 1ms";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("1200.00");
    public static final Integer DEFAULT_QUANTITY = 15;

    public static final String CATEGORY_ID = "cat-789";
    public static final String CATEGORY_NAME = "Monitores";
    public static final String CATEGORY_DESC = "Monitores e Displays";

    private ProductDomainMockFactory() {
    }

    public static Category createValidCategory() {
        return new Category(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESC);
    }

    public static Category createCategoryWithoutId() {
        return new Category(CATEGORY_NAME, CATEGORY_DESC);
    }

    public static Product createValidProduct() {
        return new Product(
                DEFAULT_ID,
                DEFAULT_SKU,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_PRICE,
                DEFAULT_QUANTITY,
                createValidCategory()
        );
    }

    public static Product createProductWithoutSku() {
        Product product = createValidProduct();
        product.setSku(null);
        return product;
    }

    public static Product createProductWithBlankSku() {
        Product product = createValidProduct();
        product.setSku("   ");
        return product;
    }

    public static Product createProductWithoutCategory() {
        Product product = createValidProduct();
        product.setCategory(null);
        return product;
    }

    public static Product createProductWithCategoryWithoutId() {
        Product product = createValidProduct();
        product.setCategory(createCategoryWithoutId());
        return product;
    }
}