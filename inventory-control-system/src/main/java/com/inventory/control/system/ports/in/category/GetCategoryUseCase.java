package com.inventory.control.system.ports.in.category;

import com.inventory.control.system.domain.model.Category;
import java.util.List;

public interface GetCategoryUseCase {
    List<Category> findAll();
    Category findById(Long id);
}