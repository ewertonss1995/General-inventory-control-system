package com.inventory.control.system.ports.out;

import com.inventory.control.system.domain.model.Category;
import java.util.Optional;
import java.util.List;

public interface CategoryRepositoryPort {
    Optional<Category> findById(String id);
    Category saveCategory(Category category);
    Category updateCategory(Category category);
    boolean existsById(String id);
    List<Category> findAll();
}