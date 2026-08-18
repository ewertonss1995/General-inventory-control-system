package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.Category;
import java.util.List;

public interface CategoryFeignPort {
    Category findById(String id);
    Category saveCategory(Category category);
    Category updateCategory(String id, Category category);
    List<Category> findAll();
}