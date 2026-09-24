package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.in.web.mapper.InventoryProductMapper;
import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductStockRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventorySaveProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryUpdateStockResponse;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public class ProductClientAdapter implements ProductFeignPort {
    
    private static final Logger log = LoggerFactory.getLogger(ProductClientAdapter.class);

    private final InventoryProductMapper mapper;
    private final InventoryFeignClient inventoryFeignClient;
    private final MeterRegistry meterRegistry;

    public ProductClientAdapter(InventoryProductMapper mapper, 
                                InventoryFeignClient inventoryFeignClient, 
                                MeterRegistry meterRegistry) {
        this.mapper = mapper;
        this.inventoryFeignClient = inventoryFeignClient;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Product saveProduct(Product product) {
        return executeWithTimer("saveProduct", () -> {
            try {
                log.debug("Iniciando processo de salvamento de produto: {}", product.getSku());
                ResponseEntity<InventorySaveProductResponse> response = inventoryFeignClient.createProduct(mapper.toInventoryProductRequest(product));
                log.debug("Produto salvo com sucesso: {}", response.getBody());
                return mapper.inventorySaveProductResponseToProduct(response.getBody());
            } catch (Exception ex) {
                recordFailure("saveProduct", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    @Override
    public Product updateProduct(String sku, Product product) {
        return executeWithTimer("updateProduct", () -> {
            try {
                log.debug("Iniciando processo de atualização de produto: {}", product.getSku());
                ResponseEntity<InventorySaveProductResponse> response = inventoryFeignClient.updateProduct(sku, mapper.toInventoryProductRequest(product));
                log.debug("Produto atualizado com sucesso: {}", response.getBody());
                return mapper.inventorySaveProductResponseToProduct(response.getBody());
            } catch (Exception ex) {
                recordFailure("updateProduct", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    @Override
    public List<Product> findAll() {
        return executeWithTimer("findAll", () -> {
            try {
                log.debug("Iniciando processo de busca de todos os produtos");
                ResponseEntity<List<InventoryProductResponse>> response = inventoryFeignClient.getAllProducts();
                
                List<InventoryProductResponse> body = response.getBody();
                if (body != null) {
                    meterRegistry.summary("client.feign.product.findall.result.size").record(body.size());
                }

                log.debug("Produtos encontrados: {}", body);
                return mapper.toProductList(body);
            } catch (Exception ex) {
                recordFailure("findAll", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }
    
    @Override
    public Product findBySku(String sku) {
        return executeWithTimer("findBySku", () -> {
            try {
                log.debug("Iniciando processo de busca de produto por SKU: {}", sku);
                ResponseEntity<InventoryProductResponse> response = inventoryFeignClient.getProductBySku(sku);
                log.debug("Produto encontrado: {}", response.getBody());
                return mapper.toProduct(response.getBody());
            } catch (Exception ex) {
                recordFailure("findBySku", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    @Override
    public UpdateStock updateProductStock(String sku, UpdateStockInput input) {
        return executeWithTimer("updateProductStock", () -> {
            try {
                log.debug("Iniciando processo de atualização de estoque do produto: {}", sku);
                InventoryProductStockRequest request = mapper.toInventoryProductStockRequest(input);
                ResponseEntity<InventoryUpdateStockResponse> response = inventoryFeignClient.updateProductStock(sku, request);
                log.debug("Estoque do produto atualizado com sucesso: {}", response.getBody());
                return mapper.toUpdateStock(sku, response.getBody());
            } catch (Exception ex) {
                recordFailure("updateProductStock", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    private <T> T executeWithTimer(String operation, Supplier<T> supplier) {
        return Timer.builder("client.feign.product.time")
                .description("Tempo de execução das chamadas HTTP via Feign no ProductClientAdapter")
                .tag("layer", "adapter_out")
                .tag("target", "inventory_core")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(supplier);
    }

    private void recordFailure(String operation, String errorType) {
        meterRegistry.counter("client.feign.product.failures",
                "layer", "adapter_out",
                "target", "inventory_core",
                "operation", operation,
                "error_type", errorType).increment();
    }
}
