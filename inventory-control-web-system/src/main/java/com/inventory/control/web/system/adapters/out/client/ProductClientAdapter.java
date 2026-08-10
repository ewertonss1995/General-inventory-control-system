package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductRequest;
import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductStockRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventorySaveProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryUpdateStockResponse;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.out.ProductFeignPort;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        ResponseEntity<InventorySaveProductResponse> response = inventoryFeignClient.createProduct(productToInventoryProductRequest(product));
        log.debug("Produto salvo com sucesso: {}", response.getBody());
        return inventorySaveProductResponseToProduct(response.getBody());
    }

    @Override
    public Product updateProduct(Product product) {
        log.debug("Iniciando processo de atualização de produto: {}", product.getSku());
        ResponseEntity<InventorySaveProductResponse> response = inventoryFeignClient.updateProduct(product.getSku(), productToInventoryProductRequest(product));
        log.debug("Produto atualizado com sucesso: {}", response.getBody());
        return inventorySaveProductResponseToProduct(response.getBody());
    }

    @Override
    public List<Product> findAll() {
        log.debug("Iniciando processo de busca de todos os produtos");
        ResponseEntity<List<InventoryProductResponse>> response = inventoryFeignClient.getAllProducts();
        log.debug("Produtos encontrados: {}", response.getBody());
        return inventoryProductResponseListToProductList(response.getBody());
    }
    
    @Override
    public Optional<Product> findBySku(String sku) {
        log.debug("Iniciando processo de busca de produto por SKU: {}", sku);
        ResponseEntity<InventoryProductResponse> response = inventoryFeignClient.getProductBySku(sku);
        log.debug("Produto encontrado: {}", response.getBody());
        return Optional.ofNullable(inventoryProductResponseToProduct(response.getBody()));
    }

    @Override
    public Map<String, String> updateProductStock(String sku, InventoryProductStockRequest request) {
        log.debug("Iniciando processo de atualização de estoque do produto: {}", sku);
        ResponseEntity<InventoryUpdateStockResponse> response = inventoryFeignClient.updateProductStock(sku, request);
        InventoryUpdateStockResponse responseBody = response.getBody();
        log.debug("Estoque do produto atualizado com sucesso: {}", responseBody);

        Map<String, String> mapResponse = new HashMap<>();
        mapResponse.put("Tipo de movimentação", responseBody.movementType());
        mapResponse.put("Valor da quantidade alterado para", responseBody.quantity().toString());
        mapResponse.put("Menssagem", responseBody.message());
        
        return mapResponse;
    }

    private Product inventorySaveProductResponseToProduct(InventorySaveProductResponse productResponse) {
        return new Product(
            productResponse.id(),
            productResponse.sku(), 
            productResponse.name(), 
            productResponse.description(),
            productResponse.price(),
            productResponse.quantity(),
            new Category(productResponse.categoryName(), null));
    }

    private Product inventoryProductResponseToProduct(InventoryProductResponse productResponse) {
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

    private List<Product> inventoryProductResponseListToProductList(List<InventoryProductResponse> productListResponse) {
        return productListResponse.stream()
        .map(this::inventoryProductResponseToProduct)
        .toList();
    }


    private InventoryProductRequest productToInventoryProductRequest(Product product) {
        return new InventoryProductRequest(
            product.getSku(), 
            product.getName(), 
            product.getDescription(), 
            product.getPrice(), 
            product.getQuantity(), 
            product.getCategory().getId());        
    }

}
