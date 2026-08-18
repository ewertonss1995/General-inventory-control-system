package com.inventory.control.system.adapters.out.database;

import com.inventory.control.system.adapters.out.exception.PersistenceException;
import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryDocument;
import com.inventory.control.system.adapters.out.database.mongodb.repository.MongoCategoryRepository;
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

    private final MongoCategoryRepository repository;

    public CategoryDatabaseAdapter(MongoCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category saveCategory(Category category) {
        log.debug("Mapeando categoria domínio para documento do MongoDB. Nome: {}", category.getName());

        try {
            CategoryDocument document = mapCategoryToDocument(category);

            log.info("Persistindo nova categoria no MongoDB. Nome: {}", category.getName());
            CategoryDocument saved = Objects.requireNonNull(repository.save(document));

            log.info("Categoria persistida com sucesso no MongoDB. ID: {} | Nome: {}", saved.getId(), saved.getName());
            return mapDocumentToCategory(saved);

        } catch (DataAccessException e) {
            log.error("Erro ao salvar categoria no MongoDB. Nome: {} | Erro: {}", category.getName(), e.getMessage(), e);
            throw new PersistenceException("Erro ao salvar categoria no banco de dados MongoDB.", e);
        }
    }

    @Override
    public Category updateCategory(Category category) {
        log.debug("Mapeando atualização de categoria para documento MongoDB. ID: {}", category.getId());

        try {
            CategoryDocument document = mapCategoryToDocument(category);

            log.info("Atualizando registro da categoria no MongoDB. ID: {} | Nome: {}", category.getId(), category.getName());
            CategoryDocument saved = Objects.requireNonNull(repository.save(document));

            log.info("Categoria atualizada com sucesso no MongoDB. ID: {} | Nome: {}", saved.getId(), saved.getName());
            return mapDocumentToCategory(saved);

        } catch (DataAccessException e) {
            log.error("Erro ao atualizar categoria no MongoDB. ID: {} | Nome: {} | Erro: {}", category.getId(), category.getName(), e.getMessage(), e);
            throw new PersistenceException("Erro ao atualizar categoria no banco de dados MongoDB.", e);
        }
    }

    @Override
    public boolean existsById(String id) {
        log.debug("Verificando existência da categoria no MongoDB pelo ID: {}", id);

        try {
            boolean exists = repository.existsById(id);

            log.debug("Resultado da verificação da categoria ID '{}': {}", id, exists);
            return exists;
        } catch (DataAccessException e) {
            log.error("Erro ao verificar existência da categoria no MongoDB. ID: {} | Erro: {}", id, e.getMessage(), e);
            throw new PersistenceException("Erro ao verificar existência da categoria no banco de dados MongoDB.", e);
        }
    }

    @Override
    public List<Category> findAll() {
        log.info("Consultando todas as categorias no MongoDB.");

        try {
            List<Category> categories = repository.findAll().stream()
                    .map(document -> {
                        log.debug("Mapeando documento MongoDB para domínio. ID: {} | Nome: {}", document.getId(), document.getName());
                        return mapDocumentToCategory(document);
                    })
                    .toList();

            log.info("Consulta de categorias finalizada com sucesso. Total de registros: {}", categories.size());
            return categories;
        } catch (DataAccessException e) {
            log.error("Erro ao consultar todas as categorias no MongoDB. Erro: {}", e.getMessage(), e);
            throw new PersistenceException("Erro ao consultar todas as categorias no banco de dados MongoDB.", e);
        }
    }

    @Override
    public Optional<Category> findById(String id) {
        log.info("Buscando categoria no MongoDB pelo ID: {}", id);

        try {
            Optional<Category> category = repository.findById(id)
                    .map(document -> {
                        log.debug("Categoria encontrada no MongoDB. Mapeando para domínio. ID: {} | Nome: {}", document.getId(), document.getName());
                        return mapDocumentToCategory(document);
                    });

            if (category.isPresent()) {
                log.info("Categoria encontrada com sucesso no MongoDB. ID: {}", id);
            } else {
                log.warn("Nenhuma categoria encontrada no MongoDB para o ID: {}", id);
            }

            return category;
        } catch (DataAccessException e) {
            log.error("Erro ao buscar categoria pelo ID no MongoDB: {}. Erro: {}", id, e.getMessage(), e);
            throw new PersistenceException("Erro ao buscar categoria pelo ID no banco de dados MongoDB.", e);
        }
    }

    private CategoryDocument mapCategoryToDocument(Category category) {
        return new CategoryDocument(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    private Category mapDocumentToCategory(CategoryDocument document) {
        return new Category(
                document.getId(),
                document.getName(),
                document.getDescription()
        );
    }
}
