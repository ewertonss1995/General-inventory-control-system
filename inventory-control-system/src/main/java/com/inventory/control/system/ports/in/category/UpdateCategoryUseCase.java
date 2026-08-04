package com.inventory.control.system.ports.in.category;

import com.inventory.control.system.domain.model.Category;

public interface UpdateCategoryUseCase {
    Category execute(Category category);
}