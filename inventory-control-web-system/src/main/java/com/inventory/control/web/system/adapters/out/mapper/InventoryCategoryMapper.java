package com.inventory.control.web.system.adapters.out.mapper;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryCategoryRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryCategoryResponse;
import com.inventory.control.web.system.domain.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryCategoryMapper {
    Category toCategory(InventoryCategoryResponse categoryResponse);
    List<Category> toCategoryList(List<InventoryCategoryResponse> categoryListResponse);
    InventoryCategoryRequest categoryToInventoryCategoryRequest(Category category);
}
