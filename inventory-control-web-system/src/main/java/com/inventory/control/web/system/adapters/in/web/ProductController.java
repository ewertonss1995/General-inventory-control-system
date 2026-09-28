package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.mapper.ProductMapper;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.ports.in.product.GetProductUseCase;
import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateStockUseCase;
import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.UpdateStockRequest;
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
        log.info("Requisição recebida para criação de produto: " + request.sku());

        Product productDomain = postProductUseCase.execute(mapper.toProduct(request));

        log.info("Produto com SKU: {} criado com sucesso. ID: {}", productDomain.getSku(), productDomain.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toSaveProductResponse(productDomain));
    }

    @PutMapping("/update/{sku}")
    public ResponseEntity<SaveProductResponse> updateProduct(
            @PathVariable String sku,
            @RequestBody @Valid ProductRequest request) {

        log.info("Requisição recebida para atualizar produto com SKU: {}", sku);

        Product productUpdated = updateProductUseCase.execute(sku, mapper.toProduct(request));

        log.info("Produto com SKU: {} atualizado com sucesso.", sku);
        return ResponseEntity.ok(mapper.toSaveProductResponse(productUpdated));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("Requisição recebida para listar todos os produtos.");

        List<ProductResponse> productResponses = mapper.toProductResponseList(getProductUseCase.findAll());

        log.info("Busca realizada com sucesso. Total de produtos encontrados: {}", productResponses.size());
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        log.info("Requisição recebida para buscar produto por SKU: {}", sku);

        Product product = getProductUseCase.findBySku(sku);

        log.info("Produto com SKU: {} localizado com sucesso.", sku);
        return ResponseEntity.ok(mapper.toProductResponse(product));
    }

    @PatchMapping("/{sku}/stock")
    public ResponseEntity<UpdateStockResponse> updateProductStock(
            @PathVariable String sku,
            @RequestBody @Valid UpdateStockRequest request) {

        log.info("Requisição recebida para alteração de estoque. SKU: {} | Tipo: {} | Quantidade: {}",
                sku, request.movementType(), request.quantity());

        UpdateStock updatedStock = updateStockUseCase.execute(sku, mapper.toUpdateStockRequest(request));

        log.info("Estoque do produto SKU: {} atualizado com sucesso. Novo saldo: {}", sku,
                updatedStock.getNewQuantity());

        return ResponseEntity.ok(mapper.toUpdateStockResponse(updatedStock));
    }
}
