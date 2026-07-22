package com.inventory.control.web.system.domain.service;

import com.inventory.control.web.system.domain.model.ProductItem;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.FetchCatalogUseCase;
import com.inventory.control.web.system.ports.in.RegisterProductUseCase;
import com.inventory.control.web.system.ports.out.InventoryClientPort;
import com.inventory.control.web.system.adapters.in.web.dto.ProductRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductCatalogService implements RegisterProductUseCase, FetchCatalogUseCase {

    private final InventoryClientPort inventoryClientPort;

    public ProductCatalogService(InventoryClientPort inventoryClientPort) {
        this.inventoryClientPort = inventoryClientPort;
    }

    @Override
    public ProductItem execute(ProductRequest productRequest) {
        Product product = new Product(
                productRequest.sku(),
                productRequest.name(),
                productRequest.description(),
                productRequest.price(),
                productRequest.quantity(),
                productRequest.categoryId()
        );
        return inventoryClientPort.createProduct(product);
    }

    @Override
    public List<ProductItem> fetchAll() {
        return inventoryClientPort.getAllProducts();
    }

    @Override
    public Optional<ProductItem> fetchBySku(String sku) {
        return inventoryClientPort.getProductBySku(sku);
    }
}