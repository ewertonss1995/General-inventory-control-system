package com.inventory.control.web.system.ports.in.category;

import com.inventory.control.web.system.domain.model.Category;
import java.util.List;

public interface GetCategoryUseCase {
    List<Category> findAll();
    Category findById(String id);
}