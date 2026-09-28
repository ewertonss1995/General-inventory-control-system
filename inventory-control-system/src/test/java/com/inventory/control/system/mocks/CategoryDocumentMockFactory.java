package com.inventory.control.system.mocks;

import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryDocument;

public final class CategoryDocumentMockFactory {

    public static final String DEFAULT_CATEGORY_ID = CategoryMockFactory.DEFAULT_CATEGORY_ID;
    public static final String DEFAULT_CATEGORY_NAME = CategoryMockFactory.DEFAULT_CATEGORY_NAME;
    public static final String DEFAULT_CATEGORY_DESCRIPTION = CategoryMockFactory.DEFAULT_CATEGORY_DESCRIPTION;

    private CategoryDocumentMockFactory() {
    }

    public static CategoryDocument createCategoryDocumentWithId() {
        return createCategoryDocument(DEFAULT_CATEGORY_ID, DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION);
    }

    public static CategoryDocument createCategoryDocumentWithoutId() {
        return createCategoryDocument(null, DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY_DESCRIPTION);
    }

    public static CategoryDocument createCategoryDocument(String id, String name, String description) {
        CategoryDocument document = new CategoryDocument();
        document.setId(id);
        document.setName(name);
        document.setDescription(description);
        return document;
    }
}