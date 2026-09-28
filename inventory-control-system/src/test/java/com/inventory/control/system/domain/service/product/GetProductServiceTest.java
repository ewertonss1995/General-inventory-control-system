package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithId;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_SKU;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_QUANTITY;
import static com.inventory.control.system.mocks.ProductMockFactory.createProduct;
import static com.inventory.control.system.mocks.ProductMockFactory.createProductWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductServiceTest {

    private static final String BLANK_STRING = "   ";
    private static final String SKU_WITH_SPACES = "  " + DEFAULT_SKU + "  ";
    private static final String SECONDARY_SKU = "TECL-LOGI-MX";

    private static final String EMPTY_SKU_EXCEPTION_MESSAGE = "O SKU informado para busca não pode ser nulo ou vazio.";
    private static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Produto não encontrado para o SKU: " + DEFAULT_SKU;

    private static final String FAILURE_COUNTER_NAME = "business.product.get.failures";
    private static final String SUMMARY_METRIC_NAME = "usecase.product.findall.result.size";

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    private MeterRegistry meterRegistry;
    private GetProductService getProductService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        getProductService = new GetProductService(productRepositoryPort, meterRegistry);
    }

    @Test
    void shouldFindAllProductsSuccessfully() {
        Product productOne = createProductWithId();
        Product productTwo = createProduct("66b4f1a2e3b0c44298fc1d00", SECONDARY_SKU, createCategoryWithId(), DEFAULT_QUANTITY);
        List<Product> expectedProducts = List.of(productOne, productTwo);

        when(productRepositoryPort.findAll()).thenReturn(expectedProducts);

        List<Product> result = getProductService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(2.0, meterRegistry.summary(SUMMARY_METRIC_NAME).max());
        verify(productRepositoryPort).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsExist() {
        when(productRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        List<Product> result = getProductService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0.0, meterRegistry.summary(SUMMARY_METRIC_NAME).max());
        verify(productRepositoryPort).findAll();
    }

    @Test
    void shouldFindProductBySkuSuccessfully() {
        Product expectedProduct = createProductWithId();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(expectedProduct));

        Product result = getProductService.findBySku(DEFAULT_SKU);

        assertNotNull(result);
        assertEquals(DEFAULT_SKU, result.getSku());
        assertEquals(expectedProduct.getName(), result.getName());
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
    }

    @Test
    void shouldFindProductBySkuWhenSkuHasSpacesAndLowerCase() {
        Product expectedProduct = createProductWithId();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(expectedProduct));

        Product result = getProductService.findBySku(SKU_WITH_SPACES);

        assertNotNull(result);
        assertEquals(DEFAULT_SKU, result.getSku());
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenSkuIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> getProductService.findBySku(null)
        );

        assertEquals(EMPTY_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("empty_sku");
        verify(productRepositoryPort, never()).findBySku(anyString());
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenSkuIsBlank() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> getProductService.findBySku(BLANK_STRING)
        );

        assertEquals(EMPTY_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("empty_sku");
        verify(productRepositoryPort, never()).findBySku(anyString());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionAndIncrementMetricWhenProductNotFound() {
        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getProductService.findBySku(DEFAULT_SKU)
        );

        assertEquals(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("product_not_found");
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
    }

    private void assertFailureMetricIncremented(String reason) {
        double count = meterRegistry.counter(
                FAILURE_COUNTER_NAME,
                "layer", "usecase",
                "reason", reason
        ).count();

        assertEquals(1.0, count);
    }
}