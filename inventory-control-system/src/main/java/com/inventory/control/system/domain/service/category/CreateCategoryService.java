package com.inventory.control.system.domain.service.category;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.in.category.CreateCategoryUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCategoryService implements CreateCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateCategoryService.class);

    private final CategoryRepositoryPort categoryRepositoryPort;

    public CreateCategoryService(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public Category execute(Category category) {
        log.info("Iniciando processo de criação de categoria. Nome: {}", category.getName());

        if (!Objects.isNull(category.getId())) {
            if (categoryRepositoryPort.existsById(category.getId())) {
                log.warn("Falha ao criar categoria: ID '{}' já está cadastrado no sistema.", category.getId());
                throw new BusinessException("ID de categoria já cadastrado: " + category.getId());
            }
        }

        Category savedCategory = categoryRepositoryPort.saveCategory(category);

        log.info("Categoria com Nome '{}' criada com sucesso. ID gerado: {}", savedCategory.getName(), savedCategory.getId());

        return savedCategory;
    }
}
