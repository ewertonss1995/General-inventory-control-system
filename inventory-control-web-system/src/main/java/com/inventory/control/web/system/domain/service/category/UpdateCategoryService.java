package com.inventory.control.web.system.domain.service.category;

import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.in.category.UpdateCategoryUseCase;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCategoryService implements UpdateCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateCategoryService.class);

    private final CategoryFeignPort categoryFeignPort;

    public UpdateCategoryService(CategoryFeignPort categoryFeignPort) {
        this.categoryFeignPort = categoryFeignPort;
    }

    @Override
    public Category execute(Category category) {
        if (Objects.isNull(category.getId())) {
            log.warn("Falha na atualização: ID da categoria é nulo.");
            throw new BusinessException("O ID da categoria é obrigatório para atualização.");
        }

        log.info("Iniciando atualização da categoria com ID: {}", category.getId());

        Category existingCategory = categoryFeignPort.findById(category.getId())
                .orElseThrow(() -> {
                    log.warn("Falha na atualização: Categoria não encontrada no banco para o ID '{}'.", category.getId());
                    return new ResourceNotFoundException("Categoria não encontrada para o ID: " + category.getId());
                });

        log.debug("Categoria de ID {} validada com sucesso.", category.getId());

        existingCategory.setDescription(category.getDescription());
        existingCategory.setName(category.getName());
        Category updatedCategory = categoryFeignPort.updateCategory(existingCategory);

        log.info("Categoria com ID '{}' atualizada com sucesso.", updatedCategory.getId());

        return updatedCategory;
    }
}
