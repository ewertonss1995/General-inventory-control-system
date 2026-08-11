package com.inventory.control.system.adapters.out.database;

import com.inventory.control.system.adapters.out.database.entities.CategoryEntity;
import com.inventory.control.system.adapters.out.database.entities.ProductEntity;
import com.inventory.control.system.adapters.out.database.repository.ProductRepository;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.out.ProductRepositoryPort;

import jakarta.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
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

        try {
            ProductEntity entity = mapProductToEntity(product);

            log.info("Persistindo novo produto no banco de dados. SKU: {}", product.getSku());
            ProductEntity saved = Objects.requireNonNull(repository.save(entity));

            log.info("Produto persistido com sucesso no banco de dados. ID: {} | SKU: {}", saved.getId(),
                    saved.getSku());

            return mapEntityToProduct(saved);

        } catch (DataAccessException e) {
            log.error("Erro ao salvar produto no banco de dados. SKU: {} | Erro: {}", product.getSku(), e.getMessage());
            throw new PersistenceException("Erro ao salvar produto no banco de dados.", e);
        }
    }

    @Override
    public Product updateProduct(Product product) {
        log.debug("Mapeando atualização de produto para entidade de banco. SKU: {}", product.getSku());

        try {
            ProductEntity entity = mapProductToEntity(product);

            log.info("Atualizando registro do produto no banco de dados. SKU: {}", product.getSku());
            ProductEntity updated = Objects.requireNonNull(repository.save(entity));

            log.info("Produto atualizado com sucesso no banco de dados. ID: {} | SKU: {}", updated.getId(),
                    updated.getSku());
                    
            return mapEntityToProduct(updated);

        } catch (DataAccessException e) {
            log.error("Erro ao atualizar produto no banco de dados. SKU: {} | Erro: {}", product.getSku(),
                    e.getMessage());
            throw new PersistenceException("Erro ao atualizar produto no banco de dados.", e);
        }
    }

    @Override
    public boolean existsBySku(String sku) {
        log.debug("Verificando existência do produto no banco pelo SKU: {}", sku);

        try {
            boolean exists = repository.existsBySkuIgnoreCase(sku);

            log.debug("Resultado da verificação do SKU '{}': {}", sku, exists);
            return exists;

        } catch (DataAccessException e) {
            log.error("Erro ao verificar a existência do produto pelo SKU: {}. Motivo: {}", sku, e.getMessage(), e);
            throw new PersistenceException("Falha ao consultar existência do produto no banco de dados.", e);
        }
    }

    @Override
    public List<Product> findAll() {
        log.info("Consultando todos os produtos na base de dados.");

        try {
            List<Product> productList = repository.findAll().stream()
                    .map(entity -> {
                        log.debug("Mapeando entidade de banco para domínio. ID: {} | SKU: {}", entity.getId(),
                                entity.getSku());
                        return mapEntityToProduct(entity);
                    })
                    .toList();

            log.debug("Consulta findAll finalizada.");
            return productList;

        } catch (DataAccessException e) {
            log.error("Erro ao consultar produtos no banco de dados. Erro: {}", e.getMessage());
            throw new PersistenceException("Erro ao consultar produtos no banco de dados.", e);
        }
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        log.debug("Buscando produto no banco de dados pelo SKU: {}", sku);

        try {
            Optional<ProductEntity> entityOptional = repository.findBySkuIgnoreCase(sku);

            if (entityOptional.isEmpty()) {
                log.debug("Nenhum produto encontrado no banco para o SKU: {}", sku);
                return Optional.empty();
            }

            ProductEntity entity = entityOptional.get();
            log.debug("Produto encontrado no banco. Mapeando para domínio. ID: {} | SKU: {}", entity.getId(),
                    entity.getSku());

            return Optional.of(mapEntityToProduct(entity));

        } catch (DataAccessException e) {
            log.error("Erro ao buscar produto no banco pelo SKU: {}. Motivo: {}", sku, e.getMessage(), e);
            throw new PersistenceException("Falha ao consultar produto no banco de dados.", e);
        }
    }

    private ProductEntity mapProductToEntity(Product productdomain) {
        CategoryEntity category = null;
        if (productdomain.getCategory() != null) {
            category = new CategoryEntity(
                    productdomain.getCategory().getId() != null ? productdomain.getCategory().getId() : null,
                    productdomain.getCategory().getName() != null ? productdomain.getCategory().getName() : null,
                    productdomain.getCategory().getDescription() != null ? productdomain.getCategory().getDescription()
                            : null);
        }

        return new ProductEntity(
                productdomain.getId() != null ? productdomain.getId() : null,
                productdomain.getSku() != null ? productdomain.getSku() : null,
                productdomain.getName() != null ? productdomain.getName() : null,
                productdomain.getDescription() != null ? productdomain.getDescription() : null,
                productdomain.getPrice() != null ? productdomain.getPrice() : null,
                productdomain.getQuantity() != null ? productdomain.getQuantity() : null,
                category);
    }

    private Product mapEntityToProduct(ProductEntity entity) {
        Category category = null;
        if (entity.getCategory() != null) {
            category = new Category(
                    entity.getCategory().getId() != null ? entity.getCategory().getId() : null,
                    entity.getCategory().getName() != null ? entity.getCategory().getName() : null,
                    entity.getCategory().getDescription() != null ? entity.getCategory().getDescription() : null);
        }

        return new Product(
                entity.getId() != null ? entity.getId() : null,
                entity.getSku() != null ? entity.getSku() : null,
                entity.getName() != null ? entity.getName() : null,
                entity.getDescription() != null ? entity.getDescription() : null,
                entity.getPrice() != null ? entity.getPrice() : null,
                entity.getQuantity() != null ? entity.getQuantity() : null,
                category);
    }
}
