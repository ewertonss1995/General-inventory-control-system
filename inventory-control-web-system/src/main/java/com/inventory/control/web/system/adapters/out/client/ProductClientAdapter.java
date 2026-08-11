package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductRequest;
import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductStockRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventorySaveProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryUpdateStockResponse;
import com.inventory.control.web.system.adapters.out.client.dto.enums.StockMovementType;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.ports.out.ProductFeignPort;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ProductClientAdapter implements ProductFeignPort {
    
    private static final Logger log = LoggerFactory.getLogger(ProductClientAdapter.class);

    private final InventoryFeignClient inventoryFeignClient;

    public ProductClientAdapter(InventoryFeignClient inventoryFeignClient) {
        this.inventoryFeignClient = inventoryFeignClient;
    }

    @Override
    public Product saveProduct(Product product) {
        log.debug("Iniciando processo de salvamento de produto: {}", product.getSku());
        ResponseEntity<InventorySaveProductResponse> response = inventoryFeignClient.createProduct(toInventoryProductRequest(product));
        log.debug("Produto salvo com sucesso: {}", response.getBody());
        return inventorySaveProductResponseToProduct(response.getBody());
    }

    @Override
    public Product updateProduct(String sku, Product product) {
        log.debug("Iniciando processo de atualização de produto: {}", product.getSku());
        ResponseEntity<InventorySaveProductResponse> response = inventoryFeignClient.updateProduct(sku, toInventoryProductRequest(product));
        log.debug("Produto atualizado com sucesso: {}", response.getBody());
        return inventorySaveProductResponseToProduct(response.getBody());
    }

    @Override
    public List<Product> findAll() {
        log.debug("Iniciando processo de busca de todos os produtos");
        ResponseEntity<List<InventoryProductResponse>> response = inventoryFeignClient.getAllProducts();
        log.debug("Produtos encontrados: {}", response.getBody());
        return toProductList(response.getBody());
    }
    
    @Override
    public Product findBySku(String sku) {
        log.debug("Iniciando processo de busca de produto por SKU: {}", sku);
        ResponseEntity<InventoryProductResponse> response = inventoryFeignClient.getProductBySku(sku);
        log.debug("Produto encontrado: {}", response.getBody());
        return toProduct(response.getBody());
    }

    @Override
    public UpdateStock updateProductStock(String sku, UpdateStockInput input) {
        log.debug("Iniciando processo de atualização de estoque do produto: {}", sku);
               
        InventoryUpdateStockResponse responseBody = inventoryFeignClient.updateProductStock(
            sku, toInventoryProductStockRequest(input))
            .getBody();
        
        log.debug("Estoque do produto atualizado com sucesso: {}", responseBody);
        
        return toUpdateStock(sku, responseBody);
    }

    private Product inventorySaveProductResponseToProduct(InventorySaveProductResponse productResponse) {
        return new Product(
            productResponse.id(),
            productResponse.sku(), 
            productResponse.name(), 
            productResponse.description(),
            productResponse.price(),
            productResponse.quantity(),
            new Category(productResponse.categoryName()));
    }

    private Product toProduct(InventoryProductResponse productResponse) {
        return new Product(
            productResponse.id(),
            productResponse.sku(), 
            productResponse.name(), 
            productResponse.description(),
            productResponse.price(),
            productResponse.quantity(),
            new Category(
                productResponse.category().id(),
                productResponse.category().name(),
                productResponse.category().description()
            )
        );
    }

    private List<Product> toProductList(List<InventoryProductResponse> productListResponse) {
        return productListResponse.stream()
        .map(this::toProduct)
        .toList();
    }

    private InventoryProductRequest toInventoryProductRequest(Product product) {
        return new InventoryProductRequest(
            product.getSku(), 
            product.getName(), 
            product.getDescription(), 
            product.getPrice(), 
            product.getQuantity(), 
            product.getCategory().getId());        
    }

    private InventoryProductStockRequest toInventoryProductStockRequest(UpdateStockInput input) {
        return new InventoryProductStockRequest(
            input.quantity(), 
            StockMovementType.valueOf(input.movementType().toUpperCase()));
    }

    private UpdateStock toUpdateStock(String sku, InventoryUpdateStockResponse response) {
        return new UpdateStock(sku, response.quantity(), response.movementType(), response.message());
    }

}
