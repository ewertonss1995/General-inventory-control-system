package com.inventory.control.system.domain.service.category;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.in.category.CreateCategoryUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CreateCategoryService implements CreateCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateCategoryService.class);

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final MeterRegistry meterRegistry;

    public CreateCategoryService(CategoryRepositoryPort categoryRepositoryPort, MeterRegistry meterRegistry) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Category execute(Category category) {
        return Timer.builder("usecase.category.time")
                .description("Tempo de execução do caso de uso de criação de categoria")
                .tag("layer", "usecase")
                .tag("operation", "createCategory")
                .register(meterRegistry)
                .record(() -> {
                    log.info("Iniciando processo de criação de categoria. Nome: {}", category.getName());

                    if (!Objects.isNull(category.getId()) && categoryRepositoryPort.existsById(category.getId())) {
                        log.warn("Falha ao criar categoria: ID '{}' já está cadastrado no sistema.", category.getId());
                        meterRegistry.counter("business.category.failures", "operation", "createCategory", "reason", "category_already_exists").increment();
                        throw new BusinessException("ID de categoria já cadastrado: " + category.getId());
                    }

                    Category savedCategory = categoryRepositoryPort.saveCategory(category);
                    log.info("Categoria com Nome '{}' criada com sucesso. ID gerado: {}", savedCategory.getName(), savedCategory.getId());

                    return savedCategory;
                });
    }
}
