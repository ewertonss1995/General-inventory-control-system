package com.inventory.control.system.domain.service.category;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class GetCategoryService implements GetCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetCategoryService.class);

    private final CategoryRepositoryPort categoryRepositoryPort;

    public GetCategoryService(CategoryRepositoryPort categoryRepositoryPort) {
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public List<Category> findAll() {
        log.info("Executando caso de uso para listar todos os produtos.");

        List<Category> categories = categoryRepositoryPort.findAll();

        log.info("Consulta de categorias concluída. Total retornado: {}", categories.size());
        return categories;
    }

    @Override
    public Category findById(String id) {
        if (Objects.isNull(id)) {
            log.warn("Tentativa de busca com ID nulo.");
            throw new BusinessException("O ID informado para busca de categoria não pode ser nulo.");
        }

        String categoryId = id.trim();
        return categoryRepositoryPort.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Falha na busca de categoria: ID '{}' não encontrado.", categoryId);
                    return new ResourceNotFoundException("Categoria não encontrada para o ID: " + categoryId);
                });
    }
}
