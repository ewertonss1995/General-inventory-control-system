package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ProductClientAdapter implements ProductFeignPort {

    private final InventoryFeignClient feignClient;

    public ProductClientAdapter(InventoryFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public Product saveProduct(Product product) {
        return null;
    }

    @Override
    public Product updateProduct(Product product) {
        return null;
    }

    @Override
    public boolean existsBySku(String sku) {
        return false;
    }

    @Override
    public List<Product> findAll() {
        return null;
    }
    
    @Override
    public Optional<Product> findBySku(String sku) {
        return null;
    }
}
