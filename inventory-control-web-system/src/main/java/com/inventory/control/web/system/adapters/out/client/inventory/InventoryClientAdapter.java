package com.inventory.control.web.system.adapters.out.client.inventory;

import com.inventory.control.web.system.adapters.out.client.inventory.dto.InventoryProductRequest;
import com.inventory.control.web.system.adapters.out.client.inventory.dto.InventoryProductResponse;
import com.inventory.control.web.system.domain.model.ProductItem;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.out.InventoryClientPort;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class InventoryClientAdapter implements InventoryClientPort {

    private final InventoryFeignClient feignClient;

    public InventoryClientAdapter(InventoryFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public ProductItem createProduct(Product product) {
        InventoryProductRequest request = new InventoryProductRequest(
                product.sku(),
                product.name(),
                product.description(),
                product.price(),
                product.quantity(),
                product.categoryId()
        );
        ResponseEntity<InventoryProductResponse> response = feignClient.createProduct(request);
        return toDomain(response.getBody());
    }

@Override
public List<ProductItem> getAllProducts() {
    ResponseEntity<List<InventoryProductResponse>> response = feignClient.findAll();

    if (response.getBody() == null) {
        return List.of();
    }

    return response.getBody().stream()
            .map(this::toDomain)
            .toList();
}

    @Override
    public Optional<ProductItem> getProductBySku(String sku) {
        ResponseEntity<InventoryProductResponse> response = feignClient.findBySku(sku);
        return Optional.ofNullable(toDomain(response.getBody()));
    }

    private ProductItem toDomain(InventoryProductResponse response) {
        if (response == null) return null;
        return new ProductItem(
                response.id(),
                response.sku(),
                response.name(),
                response.description(),
                response.price(),
                response.quantity(),
                response.categoryName()
        );
    }
}