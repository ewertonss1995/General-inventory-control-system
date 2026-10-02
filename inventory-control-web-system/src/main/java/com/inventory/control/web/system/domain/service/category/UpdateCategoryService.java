package com.inventory.control.web.system.domain.service.category;

import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.in.category.UpdateCategoryUseCase;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import com.inventory.control.web.system.domain.exception.BusinessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

public class UpdateCategoryService implements UpdateCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateCategoryService.class);

    private final CategoryFeignPort categoryFeignPort;

    public UpdateCategoryService(CategoryFeignPort categoryFeignPort) {
        this.categoryFeignPort = categoryFeignPort;
    }

    @Override
    public Category execute(String id, Category category) {
        if (Objects.isNull(id) || id.isBlank()) {
            log.warn("Falha na atualização: ID da categoria é nulo ou em branco.");
            throw new BusinessException("O ID da categoria é obrigatório para atualização.");
        }

        log.info("Executando caso de uso para atualização de categoria com ID: {}", category.getId());

        String categoryId = id.trim();
        Category updatedCategory = categoryFeignPort.updateCategory(categoryId, category);

        log.info("Categoria com ID '{}' atualizada com sucesso.", updatedCategory.getId());

        return updatedCategory;
    }
}
