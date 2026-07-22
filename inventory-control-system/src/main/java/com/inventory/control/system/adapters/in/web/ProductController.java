package com.inventory.control.system.adapters.in.web;

import com.inventory.control.system.adapters.in.web.dto.ProductRequest;
import com.inventory.control.system.adapters.in.web.dto.ProductResponse;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.CreateProductUseCase;
import com.inventory.control.system.ports.in.FindProductUseCase;
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

    public ProductController(CreateProductUseCase createProductUseCase, FindProductUseCase findProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.findProductUseCase = findProductUseCase;
    }

    /**
     * Endpoint de Cadastro de Produto
     * POST /v1/products
     */
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

    /**
     * Endpoint de Listagem Geral de Produtos
     * GET /v1/products
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<ProductResponse> products = findProductUseCase.findAll().stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(products);
    }

    /**
     * Endpoint de Busca por SKU Único
     * GET /v1/products/{sku}
     */
    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> findBySku(@PathVariable String sku) {
        Product product = findProductUseCase.findBySku(sku);
        return ResponseEntity.ok(toResponse(product));
    }

    /**
     * Método Auxiliar de Conversão: Domínio (Product) -> Adapter Web (ProductResponse)
     */
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