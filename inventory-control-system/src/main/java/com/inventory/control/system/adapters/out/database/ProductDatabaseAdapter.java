package com.inventory.control.system.adapters.out.database;

import com.inventory.control.system.adapters.out.database.entities.CategoryEntity;
import com.inventory.control.system.adapters.out.database.entities.ProductEntity;
import com.inventory.control.system.adapters.out.database.repository.ProductRepository;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ProductDatabaseAdapter implements ProductRepositoryPort {

    private final ProductRepository repository;

    public ProductDatabaseAdapter(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setSku(product.getSku());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setQuantity(product.getQuantity());
        
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(product.getCategory().getId());
        entity.setCategory(categoryEntity);

        ProductEntity saved = repository.save(entity);
        
        return new Product(saved.getId(), saved.getSku(), saved.getName(), saved.getDescription(), saved.getPrice(), saved.getQuantity(), product.getCategory());
    }

    @Override
    public boolean existsBySku(String sku) {
        return repository.existsBySkuIgnoreCase(sku);
    }

    @Override
    public List<Product> findAll() { return List.of(); }

    @Override
    public Optional<Product> findBySku(String sku) {
        return repository.findBySkuIgnoreCase(sku)
            .map(entity -> {
                Category category = null;
                if (entity.getCategory() != null) {
                    category = new Category(
                            entity.getCategory().getId(),
                            entity.getCategory().getName(),
                            entity.getCategory().getDescription()
                    );
                }

                return new Product(
                        entity.getId(),
                        entity.getSku(),
                        entity.getName(),
                        entity.getDescription(),
                        entity.getPrice(),
                        entity.getQuantity(),
                        category
                );
            });
    }
}