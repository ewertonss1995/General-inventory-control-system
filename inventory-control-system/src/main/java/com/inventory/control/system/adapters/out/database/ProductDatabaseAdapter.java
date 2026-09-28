package com.inventory.control.system.adapters.out.database;

import com.inventory.control.system.adapters.out.exception.PersistenceException;
import com.inventory.control.system.adapters.out.database.mongodb.documents.CategoryInfo;
import com.inventory.control.system.adapters.out.database.mongodb.documents.ProductDocument;
import com.inventory.control.system.adapters.out.database.mongodb.repository.MongoProductRepository;
import com.inventory.control.system.adapters.out.database.postgres.repository.PostgresStockRepository;
import com.inventory.control.system.adapters.out.database.postgres.entities.StockBalanceEntity;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.out.ProductRepositoryPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ProductDatabaseAdapter implements ProductRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(ProductDatabaseAdapter.class);

    private final MongoProductRepository mongoRepository;
    private final PostgresStockRepository postgresRepository;

    public ProductDatabaseAdapter(MongoProductRepository mongoRepository, PostgresStockRepository postgresRepository) {
        this.mongoRepository = mongoRepository;
        this.postgresRepository = postgresRepository;
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        log.debug("Mapeando produto do domínio para documento MongoDB e entidade PostgreSQL. SKU: {}", product.getSku());

        try {
            ProductDocument document = mapDomainToDocument(product);
            log.info("Persistindo dados de catálogo do produto no MongoDB. SKU: {}", product.getSku());
            ProductDocument savedDocument = Objects.requireNonNull(mongoRepository.save(document));
            log.info("Catálogo persistido no MongoDB com sucesso. Mongo ID: {} | SKU: {}", savedDocument.getId(), savedDocument.getSku());

            StockBalanceEntity stockEntity = new StockBalanceEntity(
                    savedDocument.getId(),
                    savedDocument.getSku(),
                    product.getQuantity() != null ? product.getQuantity() : 0
            );
            log.info("Persistindo saldo de estoque no PostgreSQL. ProductID: {} | SKU: {}", savedDocument.getId(), savedDocument.getSku());
            StockBalanceEntity savedStock = Objects.requireNonNull(postgresRepository.save(stockEntity));
            log.info("Saldo persistido no PostgreSQL com sucesso. Stock Balance ID: {} | Qtd: {}", savedStock.getId(), savedStock.getQuantity());

            return mapToDomain(savedDocument, savedStock.getQuantity());

        } catch (DataAccessException e) {
            log.error("Erro ao salvar produto na persistência poliglota. SKU: {} | Erro: {}", product.getSku(), e.getMessage(), e);
            throw new PersistenceException("Erro ao salvar produto no banco de dados (MongoDB/PostgreSQL).", e);
        }
    }

    @Override
    public boolean existsBySku(String sku) {
        log.debug("Verificando existência do produto no MongoDB pelo SKU: {}", sku);

        try {
            boolean exists = mongoRepository.existsBySkuIgnoreCase(sku);
            log.debug("Resultado da verificação do SKU '{}' no MongoDB: {}", sku, exists);
            return exists;

        } catch (DataAccessException e) {
            log.error("Erro ao verificar a existência do produto pelo SKU: {}. Motivo: {}", sku, e.getMessage(), e);
            throw new PersistenceException("Falha ao consultar existência do produto no MongoDB.", e);
        }
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        log.debug("Buscando produto composto (Mongo + Postgres) pelo SKU: {}", sku);

        try {
            Optional<ProductDocument> docOpt = mongoRepository.findBySkuIgnoreCase(sku);

            if (docOpt.isEmpty()) {
                log.debug("Nenhum produto encontrado no MongoDB para o SKU: {}", sku);
                return Optional.empty();
            }

            ProductDocument doc = docOpt.get();
            log.debug("Produto encontrado no MongoDB. ID: {}. Consultando saldo no PostgreSQL...", doc.getId());

            Integer quantity = postgresRepository.findByProductId(doc.getId())
                    .map(StockBalanceEntity::getQuantity)
                    .orElseGet(() -> {
                        log.warn("Nenhum registro de estoque encontrado no PostgreSQL para o Product ID: {}. Assumindo saldo 0.", doc.getId());
                        return 0;
                    });

            return Optional.of(mapToDomain(doc, quantity));

        } catch (DataAccessException e) {
            log.error("Erro ao buscar produto composto pelo SKU: {}. Motivo: {}", sku, e.getMessage(), e);
            throw new PersistenceException("Falha ao consultar produto na persistência poliglota.", e);
        }
    }

    @Override
    public List<Product> findAll() {
        log.info("Consultando todos os produtos na base de dados poliglota.");

        try {
            List<Product> products = mongoRepository.findAll().stream()
                    .map(doc -> {
                        log.debug("Buscando saldo no Postgres para o produto Mongo ID: {} | SKU: {}", doc.getId(), doc.getSku());
                        Integer quantity = postgresRepository.findByProductId(doc.getId())
                                .map(StockBalanceEntity::getQuantity)
                                .orElse(0);
                        return mapToDomain(doc, quantity);
                    })
                    .toList();

            log.info("Consulta findAll finalizada. Total de produtos retornados: {}", products.size());
            return products;

        } catch (DataAccessException e) {
            log.error("Erro ao consultar todos os produtos no banco poliglota. Erro: {}", e.getMessage(), e);
            throw new PersistenceException("Erro ao consultar lista de produtos no banco de dados.", e);
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        log.debug("Iniciando atualização do produto. SKU: {}", product.getSku());

        try {
            ProductDocument doc = mapDomainToDocument(product);
            log.info("Atualizando documento no MongoDB. SKU: {}", product.getSku());
            ProductDocument updatedDoc = Objects.requireNonNull(mongoRepository.save(doc));

            StockBalanceEntity stock = postgresRepository.findByProductId(updatedDoc.getId())
                    .orElseGet(() -> {
                        log.warn("Registro de estoque não encontrado para atualização no PostgreSQL. Criando novo saldo para ProductID: {}", updatedDoc.getId());
                        return new StockBalanceEntity(updatedDoc.getId(), updatedDoc.getSku(), 0);
                    });

            if (product.getQuantity() != null) {
                log.info("Atualizando quantidade em estoque no PostgreSQL. Novo saldo: {}", product.getQuantity());
                stock.setQuantity(product.getQuantity());
                postgresRepository.save(stock);
            }

            log.info("Produto atualizado com sucesso nas duas bases. ID: {} | SKU: {}", updatedDoc.getId(), updatedDoc.getSku());
            return mapToDomain(updatedDoc, stock.getQuantity());

        } catch (DataAccessException e) {
            log.error("Erro ao atualizar produto no banco de dados. SKU: {} | Erro: {}", product.getSku(), e.getMessage(), e);
            throw new PersistenceException("Erro ao atualizar produto no banco de dados poliglota.", e);
        }
    }

    private ProductDocument mapDomainToDocument(Product product) {
        CategoryInfo categoryInfo = null;
        if (product.getCategory() != null) {
            categoryInfo = new CategoryInfo(
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getDescription()
            );
        }

        return new ProductDocument(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                categoryInfo
        );
    }

    private Product mapToDomain(ProductDocument doc, Integer quantity) {
        Category category = null;
        if (doc.getCategory() != null) {
            category = new Category(
                    doc.getCategory().getId(),
                    doc.getCategory().getName(),
                    doc.getCategory().getDescription()
            );
        }

        return new Product(
                doc.getId(),
                doc.getSku(),
                doc.getName(),
                doc.getDescription(),
                doc.getPrice(),
                quantity,
                category
        );
    }
}
