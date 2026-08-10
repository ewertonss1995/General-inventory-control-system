package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class CategoryClientAdapter implements CategoryFeignPort {

    private final InventoryFeignClient feignClient;

    public CategoryClientAdapter(InventoryFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public Optional<Category> findById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Category saveCategory(Category category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveCategory'");
    }

    @Override
    public Category updateCategory(Category category) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCategory'");
    }

    @Override
    public boolean existsById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsById'");
    }

    @Override
    public List<Category> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

}
