package com.inventory.control.system.mocks;

import com.inventory.control.system.adapters.out.database.mongodb.documents.ProductDocument;
import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryInfo;

import java.math.BigDecimal;

public final class ProductDocumentMockFactory {

    public static final String DEFAULT_PRODUCT_ID = ProductMockFactory.DEFAULT_PRODUCT_ID;
    public static final String DEFAULT_SKU = ProductMockFactory.DEFAULT_SKU;
    public static final String DEFAULT_NAME = ProductMockFactory.DEFAULT_PRODUCT_NAME;
    public static final String DEFAULT_DESCRIPTION = ProductMockFactory.DEFAULT_PRODUCT_DESCRIPTION;
    public static final BigDecimal DEFAULT_PRICE = ProductMockFactory.DEFAULT_PRICE;
    public static final String DEFAULT_CATEGORY_ID = "66b4f1a2e3b0c44298fc1c14";
    public static final String DEFAULT_CATEGORY_NAME = "Eletrônicos";
    public static final String DEFAULT_CATEGORY_DESCRIPTION = "Dispositivos eletrônicos, componentes e acessórios";


    private ProductDocumentMockFactory() {
    }

    public static ProductDocument createProductDocumentWithId() {
        return createProductDocument(DEFAULT_PRODUCT_ID, DEFAULT_SKU, DEFAULT_NAME, DEFAULT_PRICE);
    }

    public static ProductDocument createProductDocumentWithoutId() {
        return createProductDocument(null, DEFAULT_SKU, DEFAULT_NAME, DEFAULT_PRICE);
    }

    public static ProductDocument createProductDocument(String id, String sku, String name, BigDecimal price) {
        ProductDocument document = new ProductDocument();
        document.setId(id);
        document.setSku(sku);
        document.setName(name);
        document.setDescription(DEFAULT_DESCRIPTION);
        document.setPrice(price);
        document.setCategory(
            new CategoryInfo(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION));
        return document;
    }
}