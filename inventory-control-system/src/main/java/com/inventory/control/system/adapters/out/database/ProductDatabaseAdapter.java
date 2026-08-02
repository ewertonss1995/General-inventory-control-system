package com.inventory.control.system.adapters.out.database;

import com.inventory.control.system.adapters.out.database.entities.CategoryEntity;
import com.inventory.control.system.adapters.out.database.entities.ProductEntity;
import com.inventory.control.system.adapters.out.database.repository.ProductRepository;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductDatabaseAdapter implements ProductRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(ProductDatabaseAdapter.class);

    private final ProductRepository repository;

    public ProductDatabaseAdapter(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product saveProduct(Product product) {
        log.debug("Mapeando produto domínio para entidade de banco. SKU: {}", product.getSku());

        ProductEntity entity = new ProductEntity();
        entity.setSku(product.getSku());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setQuantity(product.getQuantity());
        
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(product.getCategoryId());
        categoryEntity.setName(product.getCategoryName());
        entity.setCategory(categoryEntity);

        log.info("Persistindo novo produto no banco de dados. SKU: {}", product.getSku());
        ProductEntity saved = repository.save(entity);
        log.info("Produto persistido com sucesso no banco de dados. ID: {} | SKU: {}", saved.getId(), saved.getSku());
        
        return new Product(
            saved.getId(), 
            saved.getSku(), 
            saved.getName(), 
            saved.getDescription(), 
            saved.getPrice(), 
            saved.getQuantity(), 
            saved.getCategory().getId(),
            saved.getCategory().getName()
        );
    }

    @Override
    public Product updateProduct(Product product) {
        log.debug("Mapeando atualização de produto para entidade de banco. SKU: {}", product.getSku());

        ProductEntity entity = new ProductEntity();
        entity.setSku(product.getSku());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setQuantity(product.getQuantity());
        
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(product.getCategoryId());
        categoryEntity.setName(product.getCategoryName());
        entity.setCategory(categoryEntity);

        log.info("Atualizando registro do produto no banco de dados. SKU: {}", product.getSku());
        ProductEntity updated = repository.save(entity);
        log.info("Produto atualizado com sucesso no banco de dados. ID: {} | SKU: {}", updated.getId(), updated.getSku());
        
        return new Product(
            updated.getId(), 
            updated.getSku(), 
            updated.getName(), 
            updated.getDescription(), 
            updated.getPrice(), 
            updated.getQuantity(), 
            updated.getCategory().getId(),
            updated.getCategory().getName()
        );
    }

    @Override
    public boolean existsBySku(String sku) {
        log.debug("Verificando existência do produto no banco pelo SKU: {}", sku);
        boolean exists = repository.existsBySkuIgnoreCase(sku);
        log.debug("Resultado da verificação do SKU '{}': {}", sku, exists);
        return exists;
    }

    @Override
    public List<Product> findAll() { 
        log.info("Consultando todos os produtos na base de dados.");
        List<Product> productList = repository.findAll().stream()
            .map(entity -> {
                log.debug("Mapeando entidade de banco para domínio. ID: {} | SKU: {}", entity.getId(), entity.getSku());
                
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
                        category != null ? category.getId() : null,
                        category != null ? category.getName() : null
                );
            })
            .toList();
        log.debug("Consulta findAll finalizada.");
        return productList; 
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        log.info("Buscando produto no banco de dados pelo SKU: {}", sku);

        return repository.findBySkuIgnoreCase(sku)
            .map(entity -> {
                log.debug("Produto encontrado no banco. Mapeando para domínio. ID: {} | SKU: {}", entity.getId(), entity.getSku());
                
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
                        category != null ? category.getId() : null,
                        category != null ? category.getName() : null
                );
            })
            .or(() -> {
                log.warn("Nenhum produto encontrado no banco para o SKU: {}", sku);
                return Optional.empty();
            });
    }
}
