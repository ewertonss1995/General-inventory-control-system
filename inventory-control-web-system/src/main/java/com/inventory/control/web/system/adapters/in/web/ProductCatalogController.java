package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.dto.ProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.ProductRequest;
import com.inventory.control.web.system.domain.model.ProductItem;
import com.inventory.control.web.system.ports.in.RegisterProductUseCase;
import com.inventory.control.web.system.ports.out.InventoryClientPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog/products")
public class ProductCatalogController {

    private final RegisterProductUseCase registerProductUseCase;
    private final InventoryClientPort inventoryClientPort;

    public ProductCatalogController(RegisterProductUseCase registerProductUseCase, InventoryClientPort inventoryClientPort) {
        this.registerProductUseCase = registerProductUseCase;
        this.inventoryClientPort = inventoryClientPort;
    }

    /**
     * Endpoint BFF de Cadastro de Produto
     * POST /api/v1/catalog/products
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        ProductItem item = registerProductUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(toProductResponse(item));
    }

    /**
     * Endpoint BFF de Listagem Geral de Produtos
     * GET /api/v1/catalog/products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> list = inventoryClientPort.getAllProducts().stream()
                .map(this::toProductResponse)
                .toList();

        return ResponseEntity.ok(list);
    }

    /**
     * Endpoint BFF de Detalhes do Produto por SKU
     * GET /api/v1/catalog/products/{sku}
     */
    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        return inventoryClientPort.getProductBySku(sku)
                .map(item -> ResponseEntity.ok(toProductResponse(item)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ProductResponse toProductResponse(ProductItem item) {
        return new ProductResponse(
                item.getId(),
                item.getSku(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getQuantity(),
                item.getCategoryName()
        );
    }
}