package com.inventory.control.system.adapters.in.web;

import com.inventory.control.system.adapters.in.web.api.ProductApi;
import com.inventory.control.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.system.adapters.in.web.dto.response.UpdateStockResponse;
import com.inventory.control.system.adapters.in.web.mapper.ProductMapper;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.product.CreateProductUseCase;
import com.inventory.control.system.ports.in.product.GetProductUseCase;
import com.inventory.control.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.system.ports.in.product.UpdateStockUseCase;
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
public class ProductController implements ProductApi {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductMapper mapper;
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateStockUseCase updateStockUseCase;
    private final UpdateProductUseCase updateProductUseCase;

    public ProductController(
            ProductMapper mapper,
            CreateProductUseCase createProductUseCase,
            GetProductUseCase getProductUseCase,
            UpdateStockUseCase updateStockUseCase,
            UpdateProductUseCase updateProductUseCase) {
        this.mapper = mapper;
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateStockUseCase = updateStockUseCase;
        this.updateProductUseCase = updateProductUseCase;
    }

    @Override
    @PostMapping("/save")
    public ResponseEntity<SaveProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        log.info("Requisição recebida para criar produto: {}", request.sku());

        Product productCreated = createProductUseCase.execute(mapper.toProduct(request));

        log.info("Produto com SKU: {} criado com sucesso. ID: {}", productCreated.getSku(), productCreated.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toSaveProductResponse(productCreated));
    }

    @Override
    @PutMapping("/update/{sku}")
    public ResponseEntity<SaveProductResponse> updateProduct(
            @PathVariable String sku,
            @RequestBody @Valid ProductRequest request) {

        log.info("Requisição recebida para atualizar produto com SKU: {}", sku);

        Product productUpdated = updateProductUseCase.execute(sku, mapper.toProduct(request));

        log.info("Produto com SKU: {} atualizado com sucesso.", sku);
        return ResponseEntity.ok(mapper.toSaveProductResponse(productUpdated));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("Requisição recebida para listar todos os produtos.");

        List<Product> products = getProductUseCase.findAll();

        log.info("Busca realizada com sucesso. Total de produtos encontrados: {}", products.size());
        return ResponseEntity.ok(mapper.toProductResponseList(products));
    }

    @Override
    @GetMapping("/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        log.info("Requisição recebida para buscar produto por SKU: {}", sku);

        Product product = getProductUseCase.findBySku(sku);

        log.info("Produto com SKU: {} localizado com sucesso.", sku);
        return ResponseEntity.ok(mapper.toProductResponse(product));
    }

    @Override
    @PatchMapping("/{sku}/stock")
    public ResponseEntity<UpdateStockResponse> updateProductStock(
            @PathVariable String sku,
            @RequestBody @Valid UpdateStockRequest request) {

        log.info("Requisição recebida para alteração de estoque. SKU: {} | Tipo: {} | Quantidade: {}",
                sku, request.movementType(), request.quantity());

        Product updatedProduct = updateStockUseCase.execute(sku, mapper.toUpdateStockInput(request));

        log.info("Estoque do produto SKU: {} atualizado com sucesso. Novo saldo: {}", sku,
                updatedProduct.getQuantity());

        return ResponseEntity.ok(mapper.toUpdateStockResponse(request, updatedProduct));
    }
}