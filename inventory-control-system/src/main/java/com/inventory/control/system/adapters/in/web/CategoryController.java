package com.inventory.control.system.adapters.in.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.control.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.in.category.CreateCategoryUseCase;
import com.inventory.control.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.system.ports.in.category.UpdateCategoryUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;

    public CategoryController(
        CreateCategoryUseCase createCategoryUseCase, 
        GetCategoryUseCase getCategoryUseCase,
        UpdateCategoryUseCase updateCategoryUseCase) {
                this.createCategoryUseCase = createCategoryUseCase;
                this.getCategoryUseCase = getCategoryUseCase;
                this.updateCategoryUseCase = updateCategoryUseCase;
    }

    @PostMapping("/save")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        log.info("Requisição recebida para criar categoria: {}", request.name());

        Category category = createCategoryUseCase.execute(new Category(request.name(),request.description()));

        log.info("Categoria criada com sucesso. ID: {}", category.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toCategoryResponse(category));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest request) {

        log.info("Requisição recebida para atualizar categoria com ID: {}", id);

        Category category =  updateCategoryUseCase.execute(new Category(id, request.name(), request.description()));

        log.info("Categoria com ID: {} atualizada com sucesso.", id);
        return ResponseEntity.ok(toCategoryResponse(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.info("Requisição recebida para listar todas as categorias.");

        List<CategoryResponse> categories = getCategoryUseCase.findAll().stream()
                .map(this::toCategoryResponse)
                .toList();

        log.info("Busca realizada com sucesso. Total de categorias encontradas: {}", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("Requisição recebida para buscar categoria por ID: {}", id);

        Category category = getCategoryUseCase.findById(id);

        log.info("Categoria com ID: {} localizada com sucesso.", id);
        return ResponseEntity.ok(toCategoryResponse(category));
    }



    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
