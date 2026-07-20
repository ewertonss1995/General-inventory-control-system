package com.inventory.control.system.adapters.in.web;

import com.inventory.control.system.adapters.in.web.dto.ProductRequest;
import com.inventory.control.system.adapters.in.web.dto.ProductResponse;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.CreateProductUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest request) {
        Product product = createProductUseCase.execute(
                request.sku(), request.name(), request.description(),
                request.price(), request.quantity(), request.categoryId()
        );

        ProductResponse response = new ProductResponse(
                product.getId(), product.getSku(), product.getName(),
                product.getDescription(), product.getPrice(), product.getQuantity(),
                product.getCategory().getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}