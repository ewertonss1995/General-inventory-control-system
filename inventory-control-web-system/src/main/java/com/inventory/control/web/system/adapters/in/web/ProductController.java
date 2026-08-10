package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final PostProductUseCase postProductUseCase;

    public ProductController(PostProductUseCase postProductUseCase) {
        this.postProductUseCase = postProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        Product product = new Product(
                request.sku(),
                request.name(),
                request.description(),
                request.price(),
                request.quantity(),
                new Category(request.categoryId())
        );

        product = postProductUseCase.execute(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(toProductResponse(product));
    }

    private ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory().getName()
        );
    }
}
