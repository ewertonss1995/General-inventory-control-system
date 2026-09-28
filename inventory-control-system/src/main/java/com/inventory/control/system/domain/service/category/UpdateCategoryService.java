package com.inventory.control.system.domain.service.category;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.in.category.UpdateCategoryUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public class UpdateCategoryService implements UpdateCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateCategoryService.class);

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final MeterRegistry meterRegistry;

    public UpdateCategoryService(CategoryRepositoryPort categoryRepositoryPort, MeterRegistry meterRegistry) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Category execute(String id, Category category) {
        return executeWithTimer("updateCategory", () -> {
            if (Objects.isNull(id) || id.isBlank()) {
                log.warn("Falha na atualização: ID da categoria é nulo ou em branco.");
                recordFailure("missing_id");
                throw new BusinessException("O ID da categoria é obrigatório para atualização.");
            }

            String categoryId = id.trim();
            log.info("Iniciando atualização da categoria com ID: {}", categoryId);

            Category existingCategory = categoryRepositoryPort.findById(categoryId)
                    .orElseThrow(() -> {
                        log.warn("Falha na atualização: Categoria não encontrada no banco para o ID '{}'.", categoryId);
                        recordFailure("category_not_found");
                        return new ResourceNotFoundException("Categoria não encontrada para o ID: " + categoryId);
                    });

            log.debug("Categoria de ID {} validada com sucesso.", existingCategory.getId());

            existingCategory.setDescription(category.getDescription());
            existingCategory.setName(category.getName());

            Category updatedCategory = categoryRepositoryPort.updateCategory(existingCategory);

            log.info("Categoria com ID '{}' atualizada com sucesso.", updatedCategory.getId());

            return updatedCategory;
        });
    }

    private <T> T executeWithTimer(String operation, Supplier<T> supplier) {
        return Timer.builder("usecase.category.time")
                .description("Tempo de execução do caso de uso de atualização de categoria")
                .tag("layer", "usecase")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(supplier);
    }

    private void recordFailure(String reason) {
        meterRegistry.counter("business.category.failures",
                "layer", "usecase",
                "operation", "updateCategory",
                "reason", reason).increment();
    }
}
