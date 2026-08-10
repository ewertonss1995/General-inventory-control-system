package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryCategoryRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryCategoryResponse;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
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
        return inventoryCategoryResponseToCategory(responseBody);
    }

    @Override
    public Category updateCategory(Category category) {
        log.debug("Iniciando processo de atualização de category: {}", category.getName());
        ResponseEntity<InventoryCategoryResponse> response = inventoryFeignClient.updateCategory(category.getId(), categoryToInventoryCategoryRequest(category));
        InventoryCategoryResponse responseBody = response.getBody();
        log.debug("Category atualizada com sucesso: {}", responseBody);
        return inventoryCategoryResponseToCategory(responseBody);
    }

    @Override
    public List<Category> findAll() {
        log.debug("Iniciando processo de busca de todos os categories");
        ResponseEntity<List<InventoryCategoryResponse>> response = inventoryFeignClient.getAllCategories();
        log.debug("Categories encontrados: {}", response.getBody());
        return inventorycategoryResponseListToCategoryList(response.getBody());
    }

    @Override
    public Optional<Category> findById(Long id) {
        log.debug("Iniciando processo de busca de category por ID: {}", id);
        ResponseEntity<InventoryCategoryResponse> response = inventoryFeignClient.getCategoryById(id);
        log.debug("Category encontrado: {}", response.getBody());
        return Optional.ofNullable(inventoryCategoryResponseToCategory(response.getBody()));
    }

    private Category inventoryCategoryResponseToCategory(InventoryCategoryResponse categoryResponse) {
        return new Category(
            categoryResponse.id(), 
            categoryResponse.name(), 
            categoryResponse.description());
    }

    private List<Category> inventorycategoryResponseListToCategoryList(List<InventoryCategoryResponse> categoryListResponse) {
        return categoryListResponse.stream()
        .map(this::inventoryCategoryResponseToCategory)
        .toList();
    }

    private InventoryCategoryRequest categoryToInventoryCategoryRequest(Category category) {
        return categoryToInventoryCategoryRequest(category);

    }
}
