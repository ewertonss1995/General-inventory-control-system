package com.inventory.control.system.ports.out;

import com.inventory.control.system.domain.model.Category;
import java.util.Optional;

public interface CategoryRepositoryPort {
    Optional<Category> findById(Long id);
}