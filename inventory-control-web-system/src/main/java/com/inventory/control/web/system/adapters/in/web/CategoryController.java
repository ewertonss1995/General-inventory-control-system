package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.mapper.CategoryMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.control.web.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.web.system.ports.in.category.PostCategoryUseCase;
import com.inventory.control.web.system.ports.in.category.UpdateCategoryUseCase;

import jakarta.validation.Valid;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryMapper mapper;
    private final PostCategoryUseCase postCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;

    public CategoryController(
            CategoryMapper mapper,
            PostCategoryUseCase postCategoryUseCase,
            GetCategoryUseCase getCategoryUseCase,
            UpdateCategoryUseCase updateCategoryUseCase) {
        this.mapper = mapper;
        this.postCategoryUseCase = postCategoryUseCase;
        this.getCategoryUseCase = getCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
    }

    /**
     * @CacheEvict limpa a memória RAM de forma reativa.
     *             Quando alguém cadastrar uma nova categoria, nós forçamos o BFF a
     *             limpar o cache
     *             para que a próxima listagem busque o dado atualizado direto do
     *             backend.
     */
    @CacheEvict(value = "categories", allEntries = true)
    @PostMapping("/save")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        log.info("Requisição recebida para criar categoria: {}", request.name());

        Category category = postCategoryUseCase.execute(mapper.toCategory(request));

        log.info("Categoria criada com sucesso. ID: {}", category.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCategoryResponse(category));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable String id,
            @RequestBody @Valid CategoryRequest request) {

        log.info("Requisição recebida para atualizar categoria com ID: {}", id);

        Category category = updateCategoryUseCase.execute(id, mapper.toCategory(request));

        log.info("Categoria com ID: {} atualizada com sucesso.", id);
        return ResponseEntity.ok(mapper.toCategoryResponse(category));
    }

    /**
     * @Cacheable intercepta a chamada.
     *            Se a chave "categories" já existir na memória, ele pula a execução
     *            do método
     *            (não chama o Feign) e devolve a lista direto da RAM do BFF.
     */
    @GetMapping
    @Cacheable(value = "categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.info("Requisição recebida para listar todas as categorias.");

        List<Category> categories = getCategoryUseCase.findAll();

        log.info("Busca realizada com sucesso. Total de categorias encontradas: {}", categories.size());
        return ResponseEntity.ok(mapper.toCategoryResponseList(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable String id) {
        log.info("Requisição recebida para buscar categoria por ID: {}", id);

        Category category = getCategoryUseCase.findById(id);

        log.info("Categoria com ID: {} localizada com sucesso.", id);
        return ResponseEntity.ok(mapper.toCategoryResponse(category));
    }
}
