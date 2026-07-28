package com.inventory.control.web.system.domain.service;

import com.inventory.control.web.system.domain.model.ProductItem;
import com.inventory.control.web.system.ports.in.FetchProductUseCase;
import com.inventory.control.web.system.ports.out.FetchProductPort;
import java.util.List;
import java.util.Optional;

public class FetchProductService implements FetchProductUseCase {

    private final FetchProductPort fetchProductPort;

    public FetchProductService(FetchProductPort fetchProductPort) {
        this.fetchProductPort = fetchProductPort;
    }

    @Override
    public List<ProductItem> fetchAll() {
        return fetchProductPort.getAllProducts();
    }

    @Override
    public Optional<ProductItem> fetchBySku(String sku) {
        return fetchProductPort.getProductBySku(sku);
    }
}
