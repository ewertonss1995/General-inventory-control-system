package com.inventory.control.system.domain.service.category;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class GetCategoryService implements GetCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetCategoryService.class);

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final MeterRegistry meterRegistry;

    public GetCategoryService(CategoryRepositoryPort categoryRepositoryPort, MeterRegistry meterRegistry) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public List<Category> findAll() {
        return executeWithTimer("findAll", () -> {
            log.info("Executando caso de uso para listar todas as categorias.");

            List<Category> categories = categoryRepositoryPort.findAll();
            meterRegistry.summary("usecase.category.findall.result.size").record(categories.size());

            log.info("Consulta de categorias concluída. Total retornado: {}", categories.size());
            return categories;
        });
    }

    @Override
    public Category findById(String id) {
        return executeWithTimer("findById", () -> {
            if (Objects.isNull(id) || id.isBlank()) {
                log.warn("Tentativa de busca com ID nulo ou em branco.");
                recordFailure("findById", "missing_id");
                throw new BusinessException("O ID informado para busca de categoria não pode ser nulo.");
            }

            String categoryId = id.trim();
            return categoryRepositoryPort.findById(categoryId)
                    .orElseThrow(() -> {
                        log.warn("Falha na busca de categoria: ID '{}' não encontrado.", categoryId);
                        recordFailure("findById", "category_not_found");
                        return new ResourceNotFoundException("Categoria não encontrada para o ID: " + categoryId);
                    });
        });
    }

    private <T> T executeWithTimer(String operation, Supplier<T> supplier) {
        return Timer.builder("usecase.category.time")
                .description("Tempo de execução dos casos de uso de busca de categoria")
                .tag("layer", "usecase")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(supplier);
    }

    private void recordFailure(String operation, String reason) {
        meterRegistry.counter("business.category.failures",
                "layer", "usecase",
                "operation", operation,
                "reason", reason).increment();
    }
}
