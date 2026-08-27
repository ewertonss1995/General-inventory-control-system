package com.inventory.control.web.system.adapters.in.web.mapper;

import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.UpdateStockResponse;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.domain.model.UpdateStock;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "buildCategory") 
    Product toProduct(ProductRequest request);

    @Mapping(target = "categoryName", source = "category.name")
    SaveProductResponse toSaveProductResponse(Product product);
    
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> productList);

    UpdateStockInput toUpdateStockRequest(UpdateStockRequest request);

    UpdateStockResponse toUpdateStockResponse(UpdateStock updatedStock);

    @Named("buildCategory")
    default Category buildCategory(String categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}
