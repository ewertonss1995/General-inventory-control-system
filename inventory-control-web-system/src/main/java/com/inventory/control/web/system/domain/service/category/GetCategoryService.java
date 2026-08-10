package com.inventory.control.web.system.domain.service.category;

import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class GetCategoryService implements GetCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetCategoryService.class);

    private final CategoryFeignPort CategoryFeignPort;

    public GetCategoryService(CategoryFeignPort CategoryFeignPort) {
        this.CategoryFeignPort = CategoryFeignPort;
    }

    @Override
    public List<Category> findAll() {
        log.info("Executando caso de uso para listar todos os produtos.");

        List<Category> categories = CategoryFeignPort.findAll();

        log.info("Consulta de produtos concluída. Total retornado: {}", categories.size());
        return categories;
    }

    @Override
    public Category findById(Long id) {
        if (Objects.isNull(id)) {
            log.warn("Tentativa de busca com ID nulo.");
            throw new BusinessException("O ID informado para busca não pode ser nulo.");
        }

        return CategoryFeignPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha na busca de categoria: ID '{}' não encontrado.", id);
                    return new ResourceNotFoundException("Categoria não encontrada para o ID: " + id);
                });
    }
}
