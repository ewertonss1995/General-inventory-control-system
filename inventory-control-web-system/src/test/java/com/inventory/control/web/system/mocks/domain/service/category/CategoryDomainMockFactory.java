package com.inventory.control.web.system.mocks.domain.service.category;

import com.inventory.control.web.system.domain.model.Category;

public final class CategoryDomainMockFactory {

    public static final String DEFAULT_CATEGORY_ID = "60c72b2f9b1d8b001f8e4a11";
    public static final String DEFAULT_CATEGORY_NAME = "Eletrônicos";
    public static final String DEFAULT_CATEGORY_DESCRIPTION = "Dispositivos e acessórios eletrônicos";

    private CategoryDomainMockFactory() {
    }

    public static Category createCategoryInput() {
        return new Category(DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION);
    }

    public static Category createValidCategory() {
        return new Category(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION);
    }
}