package com.inventory.control.system.adapters.in.web;

import com.inventory.control.system.adapters.in.web.dto.ProductRequest;
import com.inventory.control.system.adapters.in.web.dto.ProductResponse;
import com.inventory.control.system.adapters.in.web.dto.UpdateStockRequest;
import com.inventory.control.system.domain.model.UpdateStockInput;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.CreateProductUseCase;
import com.inventory.control.system.ports.in.UpdateProductUseCase;
import com.inventory.control.system.ports.in.FindProductUseCase;
import com.inventory.control.system.ports.in.UpdateStockUseCase;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final CreateProductUseCase createProductUseCase;
    private final FindProductUseCase findProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final UpdateProductUseCase updateProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase, FindProductUseCase findProductUseCase, UpdateStockUseCase updateStockUseCase, UpdateProductUseCase updateProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.findProductUseCase = findProductUseCase;
        this.updateStockUseCase = updateStockUseCase;
        this.updateProductUseCase = updateProductUseCase;
    }

    @PostMapping("/save")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        log.info("Requisição recebida para criar produto com SKU: {}", request.sku());

        Product product = new Product(
                request.sku(),
                request.name(),
                request.description(),
                request.price(),
                request.quantity(),
                request.categoryId()
        );

        Product productCreated = createProductUseCase.execute(product);

        log.info("Produto com SKU: {} criado com sucesso. ID: {}", productCreated.getSku(), productCreated.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(productCreated));
    }

    @PutMapping("/update/{sku}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String sku,
            @RequestBody @Valid ProductRequest request) {

        log.info("Requisição recebida para atualizar produto com SKU: {}", sku);

        Product product = new Product(
                sku,
                request.name(),
                request.description(),
                request.price(),
                request.quantity(),
                request.categoryId()
        );

        Product productUpdated = updateProductUseCase.execute(product);

        log.info("Produto com SKU: {} atualizado com sucesso.", sku);
        return ResponseEntity.ok(toResponse(productUpdated)); // Nota: Para atualização com sucesso, o ideal é HTTP 200 OK
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        log.info("Requisição recebida para listar todos os produtos.");

        List<ProductResponse> products = findProductUseCase.findAll().stream()
                .map(this::toResponse)
                .toList();

        log.info("Busca realizada com sucesso. Total de produtos encontrados: {}", products.size());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> findBySku(@PathVariable String sku) {
        log.info("Requisição recebida para buscar produto por SKU: {}", sku);

        Product product = findProductUseCase.findBySku(sku);

        log.info("Produto com SKU: {} localizado com sucesso.", sku);
        return ResponseEntity.ok(toResponse(product));
    }

    @PatchMapping("/{sku}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable String sku,
            @RequestBody @Valid UpdateStockRequest request) {

        log.info("Requisição recebida para alteração de estoque. SKU: {} | Tipo: {} | Quantidade: {}", 
                sku, request.movementType(), request.quantity());

        UpdateStockInput input = new UpdateStockInput(sku, request.quantity(), request.movementType());
        Product updatedProduct = updateStockUseCase.execute(input);

        log.info("Estoque do produto SKU: {} atualizado com sucesso. Novo saldo: {}", sku, updatedProduct.getQuantity());
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
                product.getCategoryId(),
                product.getCategoryName()
        );
    }
}
