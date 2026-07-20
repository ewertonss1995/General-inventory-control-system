package com.inventory.control.system.domain.service;

import com.inventory.control.system.api.dto.ProductRequest;
import com.inventory.control.system.api.dto.ProductResponse;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.repository.CategoryRepository;
import com.inventory.control.system.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new IllegalArgumentException("SKU já cadastrado: " + request.sku());
        }

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        Product product = new Product(
            request.sku(), request.name(), request.description(),
            request.price(), request.quantity(), category
        );

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAllWithCategory().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
            p.getId(), p.getSku(), p.getName(), p.getDescription(),
            p.getPrice(), p.getQuantity(), p.getCategory().getName()
        );
    }
}