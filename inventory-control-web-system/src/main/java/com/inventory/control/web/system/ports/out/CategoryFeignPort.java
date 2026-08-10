package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.Category;
import java.util.Optional;
import java.util.List;

public interface CategoryFeignPort {
    Optional<Category> findById(Long id);
    Category saveCategory(Category category);
    Category updateCategory(Category category);
    List<Category> findAll();
}