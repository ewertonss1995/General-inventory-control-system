package com.inventory.control.web.system.adapters.in.web;

import com.inventory.control.web.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.web.system.domain.model.Category;

public final class CategoryWebMockFactory {

    public static final String DEFAULT_CATEGORY_ID = "66b4f1a2e3b0c44298fc1c11";
    public static final String DEFAULT_NAME = "Eletrônicos";
    public static final String DEFAULT_DESCRIPTION = "Dispositivos e periféricos eletrônicos";

    private CategoryWebMockFactory() {
    }

    public static CategoryRequest createValidCategoryRequest() {
        return new CategoryRequest(DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    public static Category createCategoryDomain() {
        Category category = new Category();
        category.setId(DEFAULT_CATEGORY_ID);
        category.setName(DEFAULT_NAME);
        category.setDescription(DEFAULT_DESCRIPTION);
        return category;
    }

    public static CategoryResponse createCategoryResponse() {
        return new CategoryResponse(DEFAULT_CATEGORY_ID, DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }
}