package com.inventory.control.web.system.domain.service.category;

import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.in.category.PostCategoryUseCase;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostCategoryService implements PostCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(PostCategoryService.class);

    private final CategoryFeignPort categoryFeignPort;

    public PostCategoryService(CategoryFeignPort categoryFeignPort) {
        this.categoryFeignPort = categoryFeignPort;
    }

    @Override
    public Category execute(Category category) {
        log.info("Iniciando processo de criação de categoria. Nome: {}", category.getName());
        Category savedCategory = categoryFeignPort.saveCategory(category);

        log.info("Categoria com Nome '{}' criada com sucesso. ID gerado: {}", savedCategory.getName(), savedCategory.getId());

        return savedCategory;
    }
}
