package com.inventory.control.system.adapters.out;

import com.inventory.control.system.adapters.in.web.mapper.ProductMapper;
import com.inventory.control.system.adapters.out.database.mongodb.documents.ProductDocument;
import com.inventory.control.system.adapters.out.database.mongodb.repository.MongoProductRepository;
import com.inventory.control.system.adapters.out.database.postgres.entities.StockBalanceEntity;
import com.inventory.control.system.adapters.out.database.postgres.repository.PostgresStockRepository;
import com.inventory.control.system.adapters.out.exception.PersistenceException;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class ProductDatabaseAdapter implements ProductRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(ProductDatabaseAdapter.class);

    private final ProductMapper mapper;
    private final MongoProductRepository mongoRepository;
    private final PostgresStockRepository postgresRepository;
    private final MeterRegistry meterRegistry;

    public ProductDatabaseAdapter(ProductMapper mapper, 
                                  MongoProductRepository mongoRepository, 
                                  PostgresStockRepository postgresRepository,
                                  MeterRegistry meterRegistry) {
        this.mapper = mapper;
        this.mongoRepository = mongoRepository;
        this.postgresRepository = postgresRepository;
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {
        return executeWithTimer("save", () -> {
            log.debug("Mapeando produto do domínio para documento MongoDB e entidade PostgreSQL. SKU: {}", product.getSku());

            try {
                ProductDocument document = mapper.toProductDocument(product);
                log.info("Persistindo dados de catálogo no MongoDB. SKU: {}", product.getSku());
                ProductDocument savedDocument = Objects.requireNonNull(mongoRepository.save(document));

                StockBalanceEntity stockEntity = StockBalanceEntity.builder()
                            .productId(savedDocument.getId())
                            .sku(savedDocument.getSku())
                            .quantity(product.getQuantity() != null ? product.getQuantity() : 0)
                            .build();

                log.info("Persistindo saldo no PostgreSQL. ProductID: {} | SKU: {}", savedDocument.getId(), savedDocument.getSku());
                StockBalanceEntity savedStock = Objects.requireNonNull(postgresRepository.save(stockEntity));

                return mapper.toProductDomain(savedDocument, savedStock.getQuantity());

            } catch (DataAccessException e) {
                recordError("saveProduct", e);
                throw new PersistenceException("Erro ao salvar produto no banco de dados (MongoDB/PostgreSQL).", e);
            }
        });
    }

    @Override
    public boolean existsBySku(String sku) {
        return executeWithTimer("existsBySku", () -> {
            log.debug("Verificando existência do produto no MongoDB pelo SKU: {}", sku);

            try {
                return mongoRepository.existsBySkuIgnoreCase(sku);
            } catch (DataAccessException e) {
                recordError("existsBySku", e);
                throw new PersistenceException("Falha ao consultar existência do produto no MongoDB.", e);
            }
        });
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return executeWithTimer("findBySku", () -> {
            log.debug("Buscando produto composto (Mongo + Postgres) pelo SKU: {}", sku);

            try {
                Optional<ProductDocument> docOpt = mongoRepository.findBySkuIgnoreCase(sku);

                if (docOpt.isEmpty()) {
                    return Optional.empty();
                }

                ProductDocument doc = docOpt.get();
                Integer quantity = postgresRepository.findByProductId(doc.getId())
                        .map(StockBalanceEntity::getQuantity)
                        .orElseGet(() -> {
                            log.warn("Nenhum registro de estoque encontrado no PostgreSQL para o Product ID: {}. Assumindo saldo 0.", doc.getId());
                            return 0;
                        });

                return Optional.of(mapper.toProductDomain(doc, quantity));

            } catch (DataAccessException e) {
                recordError("findBySku", e);
                throw new PersistenceException("Falha ao consultar produto na persistência poliglota.", e);
            }
        });
    }

    @Override
    public List<Product> findAll() {
        return executeWithTimer("findAll", () -> {
            log.info("Consultando todos os produtos na base de dados poliglota.");

            try {
                List<ProductDocument> mongoDocs = mongoRepository.findAll();
                if (mongoDocs.isEmpty()) {
                    return List.of();
                }

                List<String> productIds = mongoDocs.stream().map(ProductDocument::getId).toList();
                List<StockBalanceEntity> stocks = postgresRepository.findByProductIdIn(productIds);

                Map<String, Integer> stockMap = stocks.stream()
                        .collect(Collectors.toMap(StockBalanceEntity::getProductId, StockBalanceEntity::getQuantity));

                List<Product> products = mongoDocs.stream()
                        .map(doc -> {
                            Integer quantity = stockMap.getOrDefault(doc.getId(), 0);
                            return mapper.toProductDomain(doc, quantity);
                        })
                        .toList();

                meterRegistry.summary("db.product.findall.result.size").record(products.size());

                return products;

            } catch (DataAccessException e) {
                recordError("findAll", e);
                throw new PersistenceException("Erro ao consultar lista de produtos no banco de dados.", e);
            }
        });
    }

    @Override
    @Transactional
    public Product updateProduct(Product product) {
        return executeWithTimer("update", () -> {
            log.debug("Iniciando atualização do produto. SKU: {}", product.getSku());

            try {
                ProductDocument doc = mapper.toProductDocument(product);
                ProductDocument updatedDoc = Objects.requireNonNull(mongoRepository.save(doc));

                StockBalanceEntity stock = postgresRepository.findByProductId(updatedDoc.getId())
                        .orElseGet(() -> StockBalanceEntity.builder()
                                .productId(updatedDoc.getId())
                                .sku(updatedDoc.getSku())
                                .quantity(0)
                                .build());

                if (product.getQuantity() != null) {
                    stock.setQuantity(product.getQuantity());
                    postgresRepository.save(stock);
                }

                return mapper.toProductDomain(updatedDoc, stock.getQuantity());

            } catch (DataAccessException e) {
                recordError("updateProduct", e);
                throw new PersistenceException("Erro ao atualizar produto no banco de dados poliglota.", e);
            }
        });
    }

    private <T> T executeWithTimer(String operation, Supplier<T> supplier) {
        return Timer.builder("db.product.polyglot.time")
                .description("Tempo gasto para operação na persistência poliglota do produto")
                .tag("layer", "adapter")
                .tag("operation", operation)
                .register(meterRegistry)
                .record(supplier);
    }

    private void recordError(String operation, Exception e) {
        log.error("Erro na operação {} do banco poliglota. Erro: {}", operation, e.getMessage(), e);
        meterRegistry.counter("db.product.errors.total",
                "layer", "adapter",
                "operation", operation,
                "exception", e.getClass().getSimpleName()).increment();
    }
}
