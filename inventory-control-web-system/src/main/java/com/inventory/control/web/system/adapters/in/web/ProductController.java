package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.mapper.ProductMapper;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.ports.in.product.GetProductUseCase;
import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateStockUseCase;
import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.UpdateStockResponse;

import jakarta.validation.Valid;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductMapper mapper;
    private final GetProductUseCase getProductUseCase;
    private final PostProductUseCase postProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;

    public ProductController(
            ProductMapper mapper,
            GetProductUseCase getProductUseCase,
            PostProductUseCase postProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            UpdateStockUseCase updateStockUseCase) {
        this.mapper = mapper;
        this.getProductUseCase = getProductUseCase;
        this.postProductUseCase = postProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateStockUseCase = updateStockUseCase;
    }

    @PostMapping("/save")
    public ResponseEntity<SaveProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        log.info("Requisição recebida para criação de produto: " + request.name());

        SaveProductResponse response = toSaveProductResponse(
                postProductUseCase.execute(mapper.toProduct(request)));

        log.info("Produto com SKU: {} criado com sucesso. ID: {}", response.sku(), response.id());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update/{sku}")
    public ResponseEntity<SaveProductResponse> updateProduct(
            @PathVariable String sku,
            @RequestBody @Valid ProductRequest request) {

        log.info("Requisição recebida para atualizar produto com SKU: {}", sku);

        Product productUpdated = updateProductUseCase.execute(sku, mapper.toProduct(request));

        log.info("Produto com SKU: {} atualizado com sucesso.", sku);
        return ResponseEntity.ok(toSaveProductResponse(productUpdated));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("Requisição recebida para listar todos os produtos.");

        List<ProductResponse> productResponses = toProductResponseList(getProductUseCase.findAll());

        log.info("Busca realizada com sucesso. Total de produtos encontrados: {}", productResponses.size());
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        log.info("Requisição recebida para buscar produto por SKU: {}", sku);

        Product product = getProductUseCase.findBySku(sku);

        log.info("Produto com SKU: {} localizado com sucesso.", sku);
        return ResponseEntity.ok(toProductResponse(product));
    }

    @PatchMapping("/{sku}/stock")
    public ResponseEntity<UpdateStockResponse> updateProductStock(
            @PathVariable String sku,
            @RequestBody @Valid UpdateStockRequest request) {

        log.info("Requisição recebida para alteração de estoque. SKU: {} | Tipo: {} | Quantidade: {}",
                sku, request.movementType(), request.quantity());

        UpdateStock updatedStock = updateStockUseCase.execute(sku, toUpdateStockRequest(request));

        log.info("Estoque do produto SKU: {} atualizado com sucesso. Novo saldo: {}", sku,
                updatedStock.newQuantity());

        return ResponseEntity.ok(toUpdateStockResponse(updatedStock));
    }

    private SaveProductResponse toSaveProductResponse(Product product) {
        return new SaveProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory().getName());
    }

    private ProductResponse toProductResponse(Product product) {
        CategoryResponse categoryResponse = new CategoryResponse(
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getDescription());

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                categoryResponse);
    }

    private List<ProductResponse> toProductResponseList(List<Product> productList) {
        return productList.stream()
                .map(this::toProductResponse)
                .toList();
    }

    private UpdateStockInput toUpdateStockRequest(UpdateStockRequest request) {
            return new UpdateStockInput(request.quantity(), request.movementType());
    }

    private UpdateStockResponse toUpdateStockResponse(UpdateStock updatedStock) {
        return new UpdateStockResponse(
            updatedStock.sku(),
            updatedStock.previousQuantity(),
            updatedStock.newQuantity(),
            updatedStock.movementType(),
            updatedStock.message()
        );

    }
}
