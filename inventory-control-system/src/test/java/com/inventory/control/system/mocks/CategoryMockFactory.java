package com.inventory.control.system.mocks;

import com.inventory.control.system.domain.model.Category;

public final class CategoryMockFactory {

    public static final String DEFAULT_CATEGORY_ID = "66b4f1a2e3b0c44298fc1c14";
    public static final String DEFAULT_CATEGORY_NAME = "Eletrônicos";
    public static final String DEFAULT_CATEGORY_DESCRIPTION = "Dispositivos eletrônicos, componentes e acessórios";

    public static final String SECONDARY_CATEGORY_NAME = "Periféricos";
    public static final String SECONDARY_CATEGORY_DESCRIPTION = "Teclados, mouses, monitores e periféricos de informática";

    private CategoryMockFactory() {
    }

    public static Category createCategoryWithId() {
        return createCategory(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION);
    }

    public static Category createCategoryWithoutId() {
        return createCategory(null, DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION);
    }

    public static Category createCategory(String id, String name, String description) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);
        return category;
    }
}