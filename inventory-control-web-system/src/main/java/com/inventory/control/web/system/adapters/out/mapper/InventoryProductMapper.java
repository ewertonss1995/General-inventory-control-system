package com.inventory.control.web.system.adapters.in.web.mapper;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductRequest;
import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductStockRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventorySaveProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryUpdateStockResponse;
import com.inventory.control.web.system.adapters.out.client.dto.enums.StockMovementType;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InventoryProductMapper {

    @Mapping(target = "category", source = "categoryName", qualifiedByName = "buildCategory")
    Product inventorySaveProductResponseToProduct(InventorySaveProductResponse productResponse);

    Product toProduct(InventoryProductResponse productResponse);

    List<Product> toProductList(List<InventoryProductResponse> productListResponse);

    @Mapping(target = "categoryId", source = "category.id")
    InventoryProductRequest toInventoryProductRequest(Product product);

    InventoryProductStockRequest toInventoryProductStockRequest(UpdateStockInput input);

    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "previousQuantity", source = "response.previousQuantity")
    @Mapping(target = "newQuantity", source = "response.newQuantity")
    @Mapping(target = "movementType", source = "response.movementType")
    @Mapping(target = "message", source = "response.message")
    UpdateStock toUpdateStock(String sku, InventoryUpdateStockResponse response);

    @Named("buildCategory")
    default Category buildCategory(String categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setName(categoryId);
        return category;
    }
}
