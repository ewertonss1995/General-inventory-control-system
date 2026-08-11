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
    public Category execute(Long id, Category category) {
        log.info("Executando caso de uso para atualização de categoria com ID: {}", category.getId());
;
        Category updatedCategory = categoryFeignPort.updateCategory(id, category);

        log.info("Categoria com ID '{}' atualizada com sucesso.", updatedCategory.getId());

        return updatedCategory;
    }
}
