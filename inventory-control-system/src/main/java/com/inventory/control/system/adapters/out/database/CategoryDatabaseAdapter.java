package com.inventory.control.system.adapters.out.database;

import com.inventory.control.system.adapters.out.database.entities.CategoryEntity;
import com.inventory.control.system.adapters.out.database.repository.CategoryRepository;
import com.inventory.control.system.adapters.out.exception.PersistenceException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
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

        try {
            CategoryEntity entity = mapCategoryToEntity(category);

            log.info("Persistindo nova categoria no banco de dados. Nome: {}", category.getName());
            CategoryEntity saved = Objects.requireNonNull(repository.save(entity));
            
            log.info("Categoria persistida com sucesso no banco de dados. ID: {} | Nome: {}", saved.getId(), saved.getName());
            return mapEntityToCategory(saved);

        } catch (DataAccessException e) {
            log.error("Erro ao salvar categoria no banco de dados. Nome: {} | Erro: {}", category.getName(), e.getMessage());
            throw new PersistenceException("Erro ao salvar categoria no banco de dados.", e);
        }
    
    }

    @Override
    public Category updateCategory(Category category) {
        log.debug("Mapeando atualização de categoria para entidade de banco. ID: {}", category.getId());

        try {
            CategoryEntity entity = mapCategoryToEntity(category);

            log.info("Atualizando registro da categoria no banco de dados. ID: {} | Nome: {}", category.getId(), category.getName());
            CategoryEntity saved = Objects.requireNonNull(repository.save(entity));

            log.info("Categoria atualizada com sucesso no banco de dados. ID: {} | Nome: {}", saved.getId(), saved.getName());
            return mapEntityToCategory(saved);

        } catch (DataAccessException e) {
            log.error("Erro ao atualizar categoria no banco de dados. ID: {} | Nome: {} | Erro: {}", category.getId(), category.getName(), e.getMessage());
            throw new PersistenceException("Erro ao atualizar categoria no banco de dados.", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Verificando existência da categoria no banco pelo ID: {}", id);
        
        try {
            boolean exists = repository.existsById(id);
            
            log.debug("Resultado da verificação da categoria ID '{}': {}", id, exists);
            return exists;
        } catch (DataAccessException e) {
            log.error("Erro ao verificar existência da categoria no banco de dados. ID: {} | Erro: {}", id, e.getMessage());
            throw new PersistenceException("Erro ao verificar existência da categoria no banco de dados.", e);
        }
    }

    @Override
    public List<Category> findAll() { 
        log.info("Consultando todas as categorias na base de dados.");
        
        try {
            List<Category> categories = repository.findAll().stream()
                .map(entity -> {
                    log.debug("Mapeando entidade para domínio. ID: {} | Nome: {}", entity.getId(), entity.getName());
                    return mapEntityToCategory(entity);
                })
                .toList();

            log.info("Consulta de categorias finalizada com sucesso. Total de registros: {}", categories.size());
            return categories;
        } catch (DataAccessException e) {
            log.error("Erro ao consultar todas as categorias no banco de dados. Erro: {}", e.getMessage());
            throw new PersistenceException("Erro ao consultar todas as categorias no banco de dados.", e);
        }
    }

    @Override
    public Optional<Category> findById(Long id) {
        log.info("Buscando categoria no banco de dados pelo ID: {}", id);

        try {
            Optional<Category> category = repository.findById(id)
                .map(entity -> {
                    log.debug("Categoria encontrada. Mapeando para domínio. ID: {} | Nome: {}", entity.getId(), entity.getName());
                    return mapEntityToCategory(entity);
                });

            if (category.isPresent()) {
                log.info("Categoria encontrada com sucesso no banco de dados. ID: {}", id);
            } else {
                log.warn("Nenhuma categoria encontrada no banco de dados para o ID: {}", id);
            }

            return category;
        } catch (DataAccessException e) {
            log.error("Erro ao buscar categoria pelo ID: {}. Erro: {}", id, e.getMessage());
            throw new PersistenceException("Erro ao buscar categoria pelo ID no banco de dados.", e);
        }
    }

    private CategoryEntity mapCategoryToEntity(Category category) {
        CategoryEntity entity = new CategoryEntity();
        if (category.getId() != null) {
            entity.setId(category.getId());
        }
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        return entity;
    }

    private Category mapEntityToCategory(CategoryEntity entity) {
        return new Category(
            entity.getId(),
            entity.getName(),
            entity.getDescription()
        );
    }
}
