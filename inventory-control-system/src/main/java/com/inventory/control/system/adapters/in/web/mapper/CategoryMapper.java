package com.inventory.control.system.adapters.in.web.mapper;

import com.inventory.control.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryDocument;
import com.inventory.control.system.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // === Mapeamentos da Camada REST ===

    @Mapping(target = "id", ignore = true)
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategoryResponse(Category category);

    List<CategoryResponse> toCategoryResponseList(List<Category> categoryList);

    
    // === Mapeamentos da Camada de Persistência (MongoDB) ===

    @Mapping(target = "createdAt", ignore = true) 
    CategoryDocument toDocument(Category category);

    Category toDomain(CategoryDocument document);
}
