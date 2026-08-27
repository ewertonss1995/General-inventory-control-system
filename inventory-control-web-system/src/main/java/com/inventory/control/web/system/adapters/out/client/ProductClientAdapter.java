package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.in.web.mapper.InventoryProductMapper;
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

    private final InventoryProductMapper mapper;
    private final InventoryFeignClient inventoryFeignClient;

    public ProductClientAdapter(InventoryProductMapper mapper, InventoryFeignClient inventoryFeignClient) {
        this.mapper = mapper;
        this.inventoryFeignClient = inventoryFeignClient;
    }

    @Override
    public Product saveProduct(Product product) {
        log.debug("Iniciando processo de salvamento de produto: {}", product.getSku());
        ResponseEntity<InventorySaveProductResponse> response = inventoryFeignClient.createProduct(mapper.toInventoryProductRequest(product));
        log.debug("Produto salvo com sucesso: {}", response.getBody());
        return mapper.inventorySaveProductResponseToProduct(response.getBody());
    }

    @Override
    public Product updateProduct(String sku, Product product) {
        log.debug("Iniciando processo de atualização de produto: {}", product.getSku());
        ResponseEntity<InventorySaveProductResponse> response = inventoryFeignClient.updateProduct(sku, mapper.toInventoryProductRequest(product));
        log.debug("Produto atualizado com sucesso: {}", response.getBody());
        return mapper.inventorySaveProductResponseToProduct(response.getBody());
    }

    @Override
    public List<Product> findAll() {
        log.debug("Iniciando processo de busca de todos os produtos");
        ResponseEntity<List<InventoryProductResponse>> response = inventoryFeignClient.getAllProducts();
        log.debug("Produtos encontrados: {}", response.getBody());
        return mapper.toProductList(response.getBody());
    }
    
    @Override
    public Product findBySku(String sku) {
        log.debug("Iniciando processo de busca de produto por SKU: {}", sku);
        ResponseEntity<InventoryProductResponse> response = inventoryFeignClient.getProductBySku(sku);
        log.debug("Produto encontrado: {}", response.getBody());
        return mapper.toProduct(response.getBody());
    }

    @Override
    public UpdateStock updateProductStock(String sku, UpdateStockInput input) {
        log.debug("Iniciando processo de atualização de estoque do produto: {}", sku);
        InventoryProductStockRequest request = mapper.toInventoryProductStockRequest(input);
        ResponseEntity<InventoryUpdateStockResponse> response = inventoryFeignClient.updateProductStock(sku, request);
        log.debug("Estoque do produto atualizado com sucesso: {}", response.getBody());
        return mapper.toUpdateStock(sku, response.getBody());
    }
}
