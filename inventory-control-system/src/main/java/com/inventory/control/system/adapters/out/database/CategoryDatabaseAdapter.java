package com.inventory.control.system.adapters.out.database;

import com.inventory.control.system.adapters.out.database.entities.CategoryEntity;
import com.inventory.control.system.adapters.out.database.repository.CategoryRepository;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryDatabaseAdapter implements CategoryRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(CategoryDatabaseAdapter.class);

    private final CategoryRepository repository;

    public CategoryDatabaseAdapter(CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category saveCategory(Category category) {
        log.debug("Mapeando categoria domínio para entidade de banco. Nome: {}", category.getName());

        CategoryEntity entity = new CategoryEntity();
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());

        log.info("Persistindo nova categoria no banco de dados. Nome: {}", category.getName());
        CategoryEntity saved = repository.save(entity);
        log.info("Categoria persistida com sucesso no banco de dados. ID: {} | Nome: {}", saved.getId(), saved.getName());

        return new Category(
            saved.getId(),
            saved.getName(),
            saved.getDescription()
        );
    }

    @Override
    public Category updateCategory(Category category) {
        log.debug("Mapeando atualização de categoria para entidade de banco. ID: {}", category.getId());

        CategoryEntity entity = new CategoryEntity();
        if (category.getId() != null) {
            entity.setId(category.getId());
        }
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());

        log.info("Atualizando registro da categoria no banco de dados. ID: {} | Nome: {}", category.getId(), category.getName());
        CategoryEntity saved = repository.save(entity);
        log.info("Categoria atualizada com sucesso no banco de dados. ID: {} | Nome: {}", saved.getId(), saved.getName());

        return new Category(
            saved.getId(),
            saved.getName(),
            saved.getDescription()
        );
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Verificando existência da categoria no banco pelo ID: {}", id);
        boolean exists = repository.existsById(id);
        log.debug("Resultado da verificação da categoria ID '{}': {}", id, exists);
        return exists;
    }

    @Override
    public List<Category> findAll() { 
        log.info("Consultando todas as categorias na base de dados.");
        
        List<Category> categories = repository.findAll().stream()
            .map(entity -> new Category(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
            ))
            .toList();

        log.info("Consulta de categorias finalizada com sucesso. Total de registros: {}", categories.size());
        return categories;
    }

    @Override
    public Optional<Category> findById(Long id) {
        log.info("Buscando categoria no banco de dados pelo ID: {}", id);

        return repository.findById(id)
            .map(entity -> {
                log.debug("Categoria encontrada no banco. Mapeando para domínio. ID: {} | Nome: {}", entity.getId(), entity.getName());
                return new Category(
                    entity.getId(),
                    entity.getName(),
                    entity.getDescription()
                );
            })
            .or(() -> {
                log.warn("Nenhuma categoria encontrada no banco para o ID: {}", id);
                return Optional.empty();
            });
    }
}
