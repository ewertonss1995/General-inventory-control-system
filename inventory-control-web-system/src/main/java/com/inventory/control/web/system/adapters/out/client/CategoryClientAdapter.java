package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryCategoryRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryCategoryResponse;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CategoryClientAdapter implements CategoryFeignPort {

    private static final Logger log = LoggerFactory.getLogger(CategoryClientAdapter.class);

    private final InventoryFeignClient inventoryFeignClient;

    public CategoryClientAdapter(InventoryFeignClient inventoryFeignClient) {
        this.inventoryFeignClient = inventoryFeignClient;
    }

    @Override
    public Category saveCategory(Category category) {
        log.debug("Iniciando processo de salvamento de categoria: {}", category.getName());
        ResponseEntity<InventoryCategoryResponse> response = inventoryFeignClient.createCategory(categoryToInventoryCategoryRequest(category));
        InventoryCategoryResponse responseBody = response.getBody();
        log.debug("Produto salvo com sucesso: {}", responseBody);
        return toCategory(responseBody);
    }

    @Override
    public Category updateCategory(String id, Category category) {
        log.debug("Iniciando processo de atualização de category: {}", category.getName());
        ResponseEntity<InventoryCategoryResponse> response = inventoryFeignClient.updateCategory(id, categoryToInventoryCategoryRequest(category));
        InventoryCategoryResponse responseBody = response.getBody();
        log.debug("Category atualizada com sucesso: {}", responseBody);
        return toCategory(responseBody);
    }

    @Override
    public List<Category> findAll() {
        log.debug("Iniciando processo de busca de todos os categories");
        ResponseEntity<List<InventoryCategoryResponse>> response = inventoryFeignClient.getAllCategories();
        log.debug("Categories encontrados: {}", response.getBody());
        return toCategoryList(response.getBody());
    }

    @Override
    public Category findById(String id) {
        log.debug("Iniciando processo de busca de category por ID: {}", id);
        ResponseEntity<InventoryCategoryResponse> response = inventoryFeignClient.getCategoryById(id);
        log.debug("Category encontrado: {}", response.getBody());
        return toCategory(response.getBody());
    }

    private Category toCategory(InventoryCategoryResponse categoryResponse) {
        return new Category(
            categoryResponse.id(), 
            categoryResponse.name(), 
            categoryResponse.description());
    }

    private List<Category> toCategoryList(List<InventoryCategoryResponse> categoryListResponse) {
        return categoryListResponse.stream()
        .map(this::toCategory)
        .toList();
    }

    private InventoryCategoryRequest categoryToInventoryCategoryRequest(Category category) {
        return new InventoryCategoryRequest(category.getName(), category.getDescription());

    }
}
