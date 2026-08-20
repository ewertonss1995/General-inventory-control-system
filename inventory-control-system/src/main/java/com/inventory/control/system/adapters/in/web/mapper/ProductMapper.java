package com.inventory.control.system.adapters.in.web.mapper;

import com.inventory.control.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.system.adapters.in.web.dto.response.UpdateStockResponse;
import com.inventory.control.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryInfo;
import com.inventory.control.system.adapters.out.database.mongodb.documents.ProductDocument;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.UpdateStockInput;
import com.inventory.control.system.domain.model.enums.StockMovementType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // === Mapeamentos da Camada REST ===

    UpdateStockInput toUpdateStockInput(UpdateStockRequest request);

    @Mapping(target = "sku", source = "updatedProduct.sku")
    @Mapping(target = "previousQuantity", expression = "java(updatedProduct.getQuantity() + request.quantity())")
    @Mapping(target = "newQuantity", source = "updatedProduct.quantity")
    @Mapping(target = "movementType", source = "request.movementType")
    @Mapping(target = "message", constant = "Estoque atualizado com sucesso.")
    UpdateStockResponse toUpdateStockResponse(UpdateStockRequest request, Product updatedProduct);

    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryFromId")
    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductRequest request);

    @Mapping(target = "category", source = "category")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "category", source = "category")
    List<ProductResponse> toProductResponseList(List<Product> productList);

    @Mapping(target = "categoryName", source = "category.name", defaultValue = "<category_name>")
    SaveProductResponse toSaveProductResponse(Product product);

    @Named("mapCategoryFromId")
    default Category mapCategoryFromId(String categoryId) {
        if (categoryId == null) return null;
        
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    // === Mapeamentos da Camada de Persistência (MongoDB) ===

    @Mapping(target = "category", source = "category")
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "createdAt", ignore = true) 
    ProductDocument toProductDocument(Product product);

    CategoryInfo toCategoryInfo(Category category);

    @Mapping(target = "id", source = "doc.id")
    @Mapping(target = "sku", source = "doc.sku")
    @Mapping(target = "name", source = "doc.name")
    @Mapping(target = "description", source = "doc.description")
    @Mapping(target = "price", source = "doc.price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "category", source = "doc.category")
    Product toProductDomain(ProductDocument doc, Integer quantity);
}
