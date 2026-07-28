package com.inventory.control.web.system.ports.in;

import com.inventory.control.web.system.domain.model.ProductItem;
import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import java.math.BigDecimal;

public interface RegisterProductUseCase {
    ProductItem execute(ProductRequest product);
}