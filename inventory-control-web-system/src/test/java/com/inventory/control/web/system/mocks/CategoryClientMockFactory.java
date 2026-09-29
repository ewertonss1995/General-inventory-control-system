package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryCategoryRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryCategoryResponse;
import com.inventory.control.web.system.domain.model.Category;

import java.util.List;

public final class CategoryClientMockFactory {

    public static final String DEFAULT_ID = "cat-123";
    public static final String DEFAULT_NAME = "Eletrônicos";
    public static final String DEFAULT_DESCRIPTION = "Dispositivos eletrônicos e acessórios";

    private CategoryClientMockFactory() {
    }

    public static Category createCategoryDomain() {
        return new Category(DEFAULT_ID, DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    public static Category createCategoryDomainWithoutId() {
        return new Category(DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    public static InventoryCategoryRequest createInventoryCategoryRequest() {
        return new InventoryCategoryRequest(DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    public static InventoryCategoryResponse createInventoryCategoryResponse() {
        return new InventoryCategoryResponse(DEFAULT_ID, DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    public static List<InventoryCategoryResponse> createInventoryCategoryResponseList() {
        return List.of(
                new InventoryCategoryResponse("cat-1", "Eletrônicos", "Acessórios e eletrônicos"),
                new InventoryCategoryResponse("cat-2", "Livros", "Livros físicos e digitais")
        );
    }

    public static List<Category> createCategoryDomainList() {
        return List.of(
                new Category("cat-1", "Eletrônicos", "Acessórios e eletrônicos"),
                new Category("cat-2", "Livros", "Livros físicos e digitais")
        );
    }
}