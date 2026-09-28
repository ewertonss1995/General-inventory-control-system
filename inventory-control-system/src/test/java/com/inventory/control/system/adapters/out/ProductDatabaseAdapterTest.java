package com.inventory.control.system.adapters.out;

import com.inventory.control.system.adapters.in.web.mapper.ProductMapper;
import com.inventory.control.system.adapters.out.database.mongodb.documents.ProductDocument;
import com.inventory.control.system.adapters.out.database.mongodb.repository.MongoProductRepository;
import com.inventory.control.system.adapters.out.database.postgres.entities.StockBalanceEntity;
import com.inventory.control.system.adapters.out.database.postgres.repository.PostgresStockRepository;
import com.inventory.control.system.adapters.out.exception.PersistenceException;
import com.inventory.control.system.domain.model.Product;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;

import java.util.List;
import java.util.Optional;

import static com.inventory.control.system.mocks.ProductDocumentMockFactory.createProductDocumentWithId;
import static com.inventory.control.system.mocks.StockBalanceEntityMockFactory.DEFAULT_STOCK_ID;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_PRODUCT_ID;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_SKU;
import static com.inventory.control.system.mocks.ProductMockFactory.createProductWithId;
import static com.inventory.control.system.mocks.ProductMockFactory.createProductWithoutQuantity;
import static com.inventory.control.system.mocks.StockBalanceEntityMockFactory.createStockBalanceEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
class ProductDatabaseAdapterTest {

    @Mock
    private ProductMapper mapper;

    @Mock
    private MongoProductRepository mongoRepository;

    @Mock
    private PostgresStockRepository postgresRepository;

    private MeterRegistry meterRegistry;
    private ProductDatabaseAdapter adapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        adapter = new ProductDatabaseAdapter(mapper, mongoRepository, postgresRepository, meterRegistry);
    }

    @Test
    void shouldSaveProductSuccessfully() {
        Product productDomain = createProductWithId();
        ProductDocument document = createProductDocumentWithId();
        StockBalanceEntity stockEntity = createStockBalanceEntity();

        when(mapper.toProductDocument(productDomain)).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(postgresRepository.save(any(StockBalanceEntity.class))).thenReturn(stockEntity);
        when(mapper.toProductDomain(document, stockEntity.getQuantity())).thenReturn(productDomain);

        Product result = adapter.saveProduct(productDomain);

        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo(DEFAULT_SKU);

        verify(mapper).toProductDocument(productDomain);
        verify(mongoRepository).save(document);
        verify(postgresRepository).save(any(StockBalanceEntity.class));
        verify(mapper).toProductDomain(document, stockEntity.getQuantity());

        assertThat(meterRegistry.find("db.product.polyglot.time").timer()).isNotNull();
    }

    @Test
    void shouldSaveProductWithZeroQuantityWhenQuantityIsNull() {
        Product productWithoutQuantity = createProductWithoutQuantity();
        ProductDocument document = createProductDocumentWithId();
        StockBalanceEntity stockEntityWithZero = createStockBalanceEntity(DEFAULT_STOCK_ID, DEFAULT_PRODUCT_ID, DEFAULT_SKU, 0);

        when(mapper.toProductDocument(productWithoutQuantity)).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(postgresRepository.save(any(StockBalanceEntity.class))).thenReturn(stockEntityWithZero);
        when(mapper.toProductDomain(document, 0)).thenReturn(productWithoutQuantity);

        Product result = adapter.saveProduct(productWithoutQuantity);

        assertThat(result).isNotNull();
        verify(postgresRepository).save(any(StockBalanceEntity.class));
    }

    @Test
    void shouldThrowPersistenceExceptionWhenSaveFailsInMongo() {
        Product productDomain = createProductWithId();
        ProductDocument document = createProductDocumentWithId();

        when(mapper.toProductDocument(productDomain)).thenReturn(document);
        when(mongoRepository.save(document)).thenThrow(new DataRetrievalFailureException("Erro no Mongo"));

        assertThatThrownBy(() -> adapter.saveProduct(productDomain))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Erro ao salvar produto no banco de dados (MongoDB/PostgreSQL).")
                .hasCauseInstanceOf(DataRetrievalFailureException.class);

        double errorCount = meterRegistry.counter("db.product.errors.total",
                "layer", "adapter",
                "operation", "saveProduct",
                "exception", "DataRetrievalFailureException").count();

        assertThat(errorCount).isEqualTo(1.0);
    }

    @Test
    void shouldReturnTrueWhenProductExistsBySku() {
        when(mongoRepository.existsBySkuIgnoreCase(DEFAULT_SKU)).thenReturn(true);

        boolean exists = adapter.existsBySku(DEFAULT_SKU);

        assertThat(exists).isTrue();
        verify(mongoRepository).existsBySkuIgnoreCase(DEFAULT_SKU);
    }

    @Test
    void shouldThrowPersistenceExceptionWhenExistsBySkuFails() {
        doThrow(new DataRetrievalFailureException("Erro na consulta"))
                .when(mongoRepository).existsBySkuIgnoreCase(DEFAULT_SKU);

        assertThatThrownBy(() -> adapter.existsBySku(DEFAULT_SKU))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Falha ao consultar existência do produto no MongoDB.");
    }

    @Test
    void shouldFindProductBySkuSuccessfully() {
        ProductDocument document = createProductDocumentWithId();
        StockBalanceEntity stock = createStockBalanceEntity();
        Product domain = createProductWithId();

        when(mongoRepository.findBySkuIgnoreCase(DEFAULT_SKU)).thenReturn(Optional.of(document));
        when(postgresRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(stock));
        when(mapper.toProductDomain(document, stock.getQuantity())).thenReturn(domain);

        Optional<Product> result = adapter.findBySku(DEFAULT_SKU);

        assertThat(result).isPresent();
        assertThat(result.get().getSku()).isEqualTo(DEFAULT_SKU);

        verify(mongoRepository).findBySkuIgnoreCase(DEFAULT_SKU);
        verify(postgresRepository).findByProductId(DEFAULT_PRODUCT_ID);
        verify(mapper).toProductDomain(document, stock.getQuantity());
    }

    @Test
    void shouldReturnEmptyOptionalWhenProductNotFoundInMongoBySku() {
        when(mongoRepository.findBySkuIgnoreCase(DEFAULT_SKU)).thenReturn(Optional.empty());

        Optional<Product> result = adapter.findBySku(DEFAULT_SKU);

        assertThat(result).isEmpty();

        verify(mongoRepository).findBySkuIgnoreCase(DEFAULT_SKU);
        verify(postgresRepository, never()).findByProductId(any());
    }

    @Test
    void shouldReturnProductWithZeroQuantityWhenStockNotFoundInPostgres() {
        ProductDocument document = createProductDocumentWithId();
        Product domain = createProductWithId();

        when(mongoRepository.findBySkuIgnoreCase(DEFAULT_SKU)).thenReturn(Optional.of(document));
        when(postgresRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.empty());
        when(mapper.toProductDomain(document, 0)).thenReturn(domain);

        Optional<Product> result = adapter.findBySku(DEFAULT_SKU);

        assertThat(result).isPresent();
        verify(mapper).toProductDomain(document, 0);
    }

    @Test
    void shouldThrowPersistenceExceptionWhenFindBySkuFails() {
        when(mongoRepository.findBySkuIgnoreCase(DEFAULT_SKU))
                .thenThrow(new DataRetrievalFailureException("Erro na consulta poliglota"));

        assertThatThrownBy(() -> adapter.findBySku(DEFAULT_SKU))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Falha ao consultar produto na persistência poliglota.");
    }

    @Test
    void shouldFindAllProductsSuccessfully() {
        ProductDocument document = createProductDocumentWithId();
        StockBalanceEntity stock = createStockBalanceEntity();
        Product domain = createProductWithId();

        when(mongoRepository.findAll()).thenReturn(List.of(document));
        when(postgresRepository.findByProductIdIn(List.of(DEFAULT_PRODUCT_ID))).thenReturn(List.of(stock));
        when(mapper.toProductDomain(document, stock.getQuantity())).thenReturn(domain);

        List<Product> results = adapter.findAll();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSku()).isEqualTo(DEFAULT_SKU);

        verify(mongoRepository).findAll();
        verify(postgresRepository).findByProductIdIn(List.of(DEFAULT_PRODUCT_ID));
        verify(mapper).toProductDomain(document, stock.getQuantity());

        assertThat(meterRegistry.find("db.product.findall.result.size").summary().count()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsInMongo() {
        when(mongoRepository.findAll()).thenReturn(List.of());

        List<Product> results = adapter.findAll();

        assertThat(results).isEmpty();
        verify(mongoRepository).findAll();
        verify(postgresRepository, never()).findByProductIdIn(any());
    }

    @Test
    void shouldThrowPersistenceExceptionWhenFindAllFails() {
        when(mongoRepository.findAll()).thenThrow(new DataRetrievalFailureException("Erro no findAll"));

        assertThatThrownBy(() -> adapter.findAll())
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Erro ao consultar lista de produtos no banco de dados.");
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product productDomain = createProductWithId();
        ProductDocument document = createProductDocumentWithId();
        StockBalanceEntity stock = createStockBalanceEntity();

        when(mapper.toProductDocument(productDomain)).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(postgresRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(stock));
        when(postgresRepository.save(stock)).thenReturn(stock);
        when(mapper.toProductDomain(document, stock.getQuantity())).thenReturn(productDomain);

        Product result = adapter.updateProduct(productDomain);

        assertThat(result).isNotNull();
        assertThat(result.getSku()).isEqualTo(DEFAULT_SKU);

        verify(mapper).toProductDocument(productDomain);
        verify(mongoRepository).save(document);
        verify(postgresRepository).findByProductId(DEFAULT_PRODUCT_ID);
        verify(postgresRepository).save(stock);
        verify(mapper).toProductDomain(document, stock.getQuantity());
    }

    @Test
    void shouldCreateNewStockWhenUpdatingProductWithoutExistingStockInPostgres() {
        Product productDomain = createProductWithId();
        ProductDocument document = createProductDocumentWithId();

        when(mapper.toProductDocument(productDomain)).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(postgresRepository.findByProductId(DEFAULT_PRODUCT_ID)).thenReturn(Optional.empty());
        when(mapper.toProductDomain(eq(document), any())).thenReturn(productDomain);

        Product result = adapter.updateProduct(productDomain);

        assertThat(result).isNotNull();
        verify(postgresRepository).save(any(StockBalanceEntity.class));
    }

    @Test
    void shouldThrowPersistenceExceptionWhenUpdateFails() {
        Product productDomain = createProductWithId();
        ProductDocument document = createProductDocumentWithId();

        when(mapper.toProductDocument(productDomain)).thenReturn(document);
        when(mongoRepository.save(document)).thenThrow(new DataRetrievalFailureException("Erro na atualização"));

        assertThatThrownBy(() -> adapter.updateProduct(productDomain))
                .isInstanceOf(PersistenceException.class)
                .hasMessage("Erro ao atualizar produto no banco de dados poliglota.");
    }
}