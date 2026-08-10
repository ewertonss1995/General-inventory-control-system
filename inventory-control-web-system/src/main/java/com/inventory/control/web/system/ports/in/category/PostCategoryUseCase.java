package com.inventory.control.web.system.ports.in.category;

import com.inventory.control.web.system.domain.model.Category;

public interface PostCategoryUseCase {
    Category execute(Category category);
}