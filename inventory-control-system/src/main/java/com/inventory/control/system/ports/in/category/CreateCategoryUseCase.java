package com.inventory.control.system.ports.in.category;

import com.inventory.control.system.domain.model.Category;

public interface CreateCategoryUseCase {
    Category execute(Category category);
}