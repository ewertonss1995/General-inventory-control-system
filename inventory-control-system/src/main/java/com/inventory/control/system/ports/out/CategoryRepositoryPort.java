package com.inventory.control.system.ports.out;

import com.inventory.control.system.domain.model.Category;
import java.util.Optional;
import java.util.List;

public interface CategoryRepositoryPort {
    Optional<Category> findById(Long id);
    Category saveCategory(Category category);
    Category updateCategory(Category category);
    boolean existsById(Long id);
    List<Category> findAll();
}