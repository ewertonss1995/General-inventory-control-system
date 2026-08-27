package com.inventory.control.web.system.adapters.in.web.mapper;

import com.inventory.control.web.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.CategoryResponse;

import com.inventory.control.web.system.domain.model.Category;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true) 
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponseList(List<Category> categoryList);
}