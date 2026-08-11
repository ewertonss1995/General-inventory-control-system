package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.Category;
import java.util.List;

public interface CategoryFeignPort {
    Category findById(Long id);
    Category saveCategory(Category category);
    Category updateCategory(Long id, Category category);
    List<Category> findAll();
}