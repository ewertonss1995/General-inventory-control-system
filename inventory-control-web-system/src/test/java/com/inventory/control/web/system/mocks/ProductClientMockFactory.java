package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.enums.StockMovementType;
import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductStockRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryCategoryResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventorySaveProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryUpdateStockResponse;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;

import java.math.BigDecimal;
import java.util.List;

public final class ProductClientMockFactory {

    public static final String DEFAULT_ID = "prod-123";
    public static final String DEFAULT_SKU = "SKU-999";
    public static final String DEFAULT_NAME = "Teclado Mecânico";
    public static final String DEFAULT_DESCRIPTION = "Teclado RGB Switch Blue";
    public static final BigDecimal DEFAULT_PRICE = new BigDecimal("250.00");
    public static final Integer DEFAULT_QUANTITY = 50;
    public static final String DEFAULT_CATEGORY_NAME = "Eletrônicos";

    private ProductClientMockFactory() {
    }

    public static Product createProductDomain() {
        Product product = new Product();
        product.setId(DEFAULT_ID);
        product.setSku(DEFAULT_SKU);
        product.setName(DEFAULT_NAME);
        product.setDescription(DEFAULT_DESCRIPTION);
        product.setPrice(DEFAULT_PRICE);
        product.setQuantity(DEFAULT_QUANTITY);
        return product;
    }

    public static InventorySaveProductResponse createInventorySaveProductResponse() {
        return new InventorySaveProductResponse(
                DEFAULT_ID,
                DEFAULT_SKU,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_PRICE,
                DEFAULT_QUANTITY,
                DEFAULT_CATEGORY_NAME
        );
    }

    public static InventoryProductResponse createInventoryProductResponse() {
        InventoryCategoryResponse category = new InventoryCategoryResponse("cat-1", DEFAULT_CATEGORY_NAME, "Descrição Categoria");
        return new InventoryProductResponse(
                DEFAULT_ID,
                DEFAULT_SKU,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_PRICE,
                DEFAULT_QUANTITY,
                category
        );
    }

    public static List<InventoryProductResponse> createInventoryProductResponseList() {
        return List.of(createInventoryProductResponse());
    }

    public static List<Product> createProductDomainList() {
        return List.of(createProductDomain());
    }

    public static UpdateStockInput createUpdateStockInput() {
        UpdateStockInput input = new UpdateStockInput();
        input.setQuantity(10);
        input.setMovementType("IN");
        return input;
    }

    public static InventoryProductStockRequest createInventoryProductStockRequest() {
        return new InventoryProductStockRequest(10, StockMovementType.IN);
    }

    public static InventoryUpdateStockResponse createInventoryUpdateStockResponse() {
        return new InventoryUpdateStockResponse(
                DEFAULT_SKU,
                50,
                60,
                "IN",
                "Estoque atualizado com sucesso"
        );
    }

    public static UpdateStock createUpdateStockDomain() {
        UpdateStock updateStock = new UpdateStock();
        updateStock.setSku(DEFAULT_SKU);
        updateStock.setPreviousQuantity(50);
        updateStock.setNewQuantity(60);
        updateStock.setMovementType("IN");
        updateStock.setMessage("Estoque atualizado com sucesso");
        return updateStock;
    }
}