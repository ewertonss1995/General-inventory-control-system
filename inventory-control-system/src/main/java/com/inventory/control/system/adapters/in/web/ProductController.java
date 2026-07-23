package com.inventory.control.system.adapters.in.web;

import com.inventory.control.system.adapters.in.web.dto.ProductRequest;
import com.inventory.control.system.adapters.in.web.dto.ProductResponse;
import com.inventory.control.system.adapters.in.web.dto.UpdateStockRequest;
import com.inventory.control.system.domain.model.UpdateStockInput;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.CreateProductUseCase;
import com.inventory.control.system.ports.in.FindProductUseCase;
import com.inventory.control.system.ports.in.UpdateStockUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final FindProductUseCase findProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;

    public ProductController(CreateProductUseCase createProductUseCase, FindProductUseCase findProductUseCase, UpdateStockUseCase updateStockUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.findProductUseCase = findProductUseCase;
        this.updateStockUseCase = updateStockUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        Product product = createProductUseCase.execute(
                request.sku(),
                request.name(),
                request.description(),
                request.price(),
                request.quantity(),
                request.categoryId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(product));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<ProductResponse> products = findProductUseCase.findAll().stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(products);
    }


    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> findBySku(@PathVariable String sku) {
        Product product = findProductUseCase.findBySku(sku);
        return ResponseEntity.ok(toResponse(product));
    }

    @PatchMapping("/{sku}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable String sku,
            @RequestBody @Valid UpdateStockRequest request) {

        UpdateStockInput input = new UpdateStockInput(sku, request.quantity(), request.movementType());
        Product updatedProduct = updateStockUseCase.execute(input);

        return ResponseEntity.ok(toResponse(updatedProduct));
    }
    
    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory() != null ? product.getCategory().getName() : null
        );
    }
}

