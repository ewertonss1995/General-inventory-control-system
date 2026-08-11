package com.inventory.control.system.domain.service.category;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.in.category.UpdateCategoryUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCategoryService implements UpdateCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateCategoryService.class);

    private final CategoryRepositoryPort categoryRepositoryPort;

    public UpdateCategoryService(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public Category execute(Long id, Category category) {
        if (Objects.isNull(id)) {
            log.warn("Falha na atualização: ID da categoria é nulo.");
            throw new BusinessException("O ID da categoria é obrigatório para atualização.");
        }

        log.info("Iniciando atualização da categoria com ID: {}", id);

        Category existingCategory = categoryRepositoryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha na atualização: Categoria não encontrada no banco para o ID '{}'.", id);
                    return new ResourceNotFoundException("Categoria não encontrada para o ID: " + id);
                });

        log.debug("Categoria de ID {} validada com sucesso.", existingCategory.getId());

        existingCategory.setDescription(category.getDescription());
        existingCategory.setName(category.getName());

        Category updatedCategory = categoryRepositoryPort.updateCategory(existingCategory);

        log.info("Categoria com ID '{}' atualizada com sucesso.", updatedCategory.getId());

        return updatedCategory;
    }
}
