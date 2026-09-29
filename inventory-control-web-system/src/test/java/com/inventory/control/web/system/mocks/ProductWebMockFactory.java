package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.UpdateStockResponse;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;

import java.math.BigDecimal;

public final class ProductWebMockFactory {

    public static final String DEFAULT_PRODUCT_ID = "66b4f1a2e3b0c44298fc1d99";
    public static final String DEFAULT_SKU = "MON-LG-29ULTRA";
    public static final String DEFAULT_NAME = "Monitor Ultrawide LG 29";
    public static final String DEFAULT_DESCRIPTION = "Monitor IPS 29 polegadas Full HD";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("1299.90");
    public static final Integer DEFAULT_QUANTITY = 15;
    public static final String DEFAULT_CATEGORY_ID = "66b4f1a2e3b0c44298fc1c11";
    public static final String DEFAULT_CATEGORY_NAME = "Eletrônicos";

    private ProductWebMockFactory() {
    }

    public static ProductRequest createValidProductRequest() {
        return new ProductRequest(
                DEFAULT_SKU,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_CATEGORY_ID
        );
    }

    public static UpdateStockRequest createValidUpdateStockRequest() {
        return new UpdateStockRequest(5, "IN");
    }

    public static Product createProductDomain() {
        Product product = new Product();
        product.setId(DEFAULT_PRODUCT_ID);
        product.setSku(DEFAULT_SKU);
        product.setName(DEFAULT_NAME);
        product.setDescription(DEFAULT_DESCRIPTION);
        product.setPrice(DEFAULT_PRICE);
        product.setQuantity(DEFAULT_QUANTITY);
        return product;
    }

    public static SaveProductResponse createSaveProductResponse() {
        return new SaveProductResponse(
                DEFAULT_PRODUCT_ID,
                DEFAULT_SKU,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_CATEGORY_NAME
        );
    }

    public static ProductResponse createProductResponse() {
        CategoryResponse categoryResponse = new CategoryResponse(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME, "Categoria de eletrônicos");
        return new ProductResponse(
                DEFAULT_PRODUCT_ID,
                DEFAULT_SKU,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_PRICE,
                DEFAULT_QUANTITY,
                categoryResponse
        );
    }

    public static UpdateStock createUpdateStockDomain() {
        UpdateStock updateStock = new UpdateStock();
        updateStock.setSku(DEFAULT_SKU);
        updateStock.setPreviousQuantity(15);
        updateStock.setNewQuantity(20);
        updateStock.setMovementType("IN");
        updateStock.setMessage("Estoque atualizado com sucesso");
        return updateStock;
    }

    public static UpdateStockResponse createUpdateStockResponse() {
        return new UpdateStockResponse(
                DEFAULT_SKU,
                15,
                20,
                "IN",
                "Estoque atualizado com sucesso"
        );
    }
}