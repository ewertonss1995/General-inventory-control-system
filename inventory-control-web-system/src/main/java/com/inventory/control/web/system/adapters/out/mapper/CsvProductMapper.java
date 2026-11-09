package com.inventory.control.web.system.adapters.out.mapper;

import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.adapters.out.csv.dto.CsvProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(
    componentModel = "spring",
    imports = { BigDecimal.class }
)
public interface CsvProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(mapCategory(dto))")
    @Mapping(target = "price", expression = "java(new BigDecimal(dto.price()))")
    @Mapping(target = "quantity", expression = "java(Integer.parseInt(dto.quantity()))")
    Product toDomain(CsvProductDto dto);

    default Category mapCategory(CsvProductDto dto) {
        return new Category(
                dto.categoryId(),
                dto.categoryName(),
                dto.categoryDescription()
        );
    }
}