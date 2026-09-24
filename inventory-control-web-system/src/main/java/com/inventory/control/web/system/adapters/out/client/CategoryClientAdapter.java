package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.mapper.InventoryCategoryMapper;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryCategoryResponse;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public class CategoryClientAdapter implements CategoryFeignPort {

    private static final Logger log = LoggerFactory.getLogger(CategoryClientAdapter.class);

    private final InventoryCategoryMapper mapper;
    private final InventoryFeignClient inventoryFeignClient;
    private final MeterRegistry meterRegistry;

    public CategoryClientAdapter(InventoryCategoryMapper mapper,
                                 InventoryFeignClient inventoryFeignClient,
                                 MeterRegistry meterRegistry) {
        this.mapper = mapper;
        this.inventoryFeignClient = inventoryFeignClient;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Category saveCategory(Category category) {
        return executeWithTimer("saveCategory", () -> {
            try {
                log.debug("Iniciando processo de salvamento de categoria: {}", category.getName());
                ResponseEntity<InventoryCategoryResponse> response = inventoryFeignClient.createCategory(mapper.categoryToInventoryCategoryRequest(category));
                InventoryCategoryResponse responseBody = response.getBody();
                log.debug("Categoria salva com sucesso: {}", responseBody);
                return mapper.toCategory(responseBody);
            } catch (Exception ex) {
                recordFailure("saveCategory", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    @Override
    public Category updateCategory(String id, Category category) {
        return executeWithTimer("updateCategory", () -> {
            try {
                log.debug("Iniciando processo de atualização de categoria: {}", category.getName());
                ResponseEntity<InventoryCategoryResponse> response = inventoryFeignClient.updateCategory(id, mapper.categoryToInventoryCategoryRequest(category));
                InventoryCategoryResponse responseBody = response.getBody();
                log.debug("Categoria atualizada com sucesso: {}", responseBody);
                return mapper.toCategory(responseBody);
            } catch (Exception ex) {
                recordFailure("updateCategory", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    @Override
    public List<Category> findAll() {
        return executeWithTimer("findAll", () -> {
            try {
                log.debug("Iniciando processo de busca de todas as categorias");
                ResponseEntity<List<InventoryCategoryResponse>> response = inventoryFeignClient.getAllCategories();
                
                List<InventoryCategoryResponse> body = response.getBody();
                if (body != null) {
                    meterRegistry.summary("client.feign.category.findall.result.size").record(body.size());
                }

                log.debug("Categorias encontradas: {}", body);
                return mapper.toCategoryList(body);
            } catch (Exception ex) {
                recordFailure("findAll", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    @Override
    public Category findById(String id) {
        return executeWithTimer("findById", () -> {
            try {
                log.debug("Iniciando processo de busca de categoria por ID: {}", id);
                ResponseEntity<InventoryCategoryResponse> response = inventoryFeignClient.getCategoryById(id);
                InventoryCategoryResponse responseBody = response.getBody();
                log.debug("Categoria encontrada: {}", responseBody);
                return mapper.toCategory(responseBody);
            } catch (Exception ex) {
                recordFailure("findById", ex.getClass().getSimpleName());
                throw ex;
            }
        });
    }

    private <T> T executeWithTimer(String operation, Supplier<T> supplier) {
        return Timer.builder("client.feign.category.time")
                .description("Tempo de execução das chamadas HTTP via Feign no CategoryClientAdapter")
                .tag("layer", "adapter_out")
                .tag("target", "inventory_core")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(supplier);
    }

    private void recordFailure(String operation, String errorType) {
        meterRegistry.counter("client.feign.category.failures",
                "layer", "adapter_out",
                "target", "inventory_core",
                "operation", operation,
                "error_type", errorType).increment();
    }
}
