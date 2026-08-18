package com.inventory.control.web.system.domain.service.category;

import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetCategoryService implements GetCategoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetCategoryService.class);

    private final CategoryFeignPort CategoryFeignPort;

    public GetCategoryService(CategoryFeignPort CategoryFeignPort) {
        this.CategoryFeignPort = CategoryFeignPort;
    }

    @Override
    public List<Category> findAll() {
        log.info("Executando caso de uso para listar todas as categorias.");

        List<Category> categories = CategoryFeignPort.findAll();

        log.info("Consulta de categorias concluída. Total retornado: {}", categories.size());
        
        return categories;
    }

    @Override
    public Category findById(String id) {
        log.info("Executando caso de uso para listar categoria ID {}.", id);

        String categoryId = id.trim();
        Category category = CategoryFeignPort.findById(categoryId);

        log.info("Consulta de categoria ID {} concluida.", category.getId());

        return category;
    }
}
