package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.inventory.control.system.mocks.CategoryMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithId;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_LOWERCASE_SKU;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_SKU;
import static com.inventory.control.system.mocks.ProductMockFactory.createProduct;
import static com.inventory.control.system.mocks.ProductMockFactory.createProductWithId;
import static com.inventory.control.system.mocks.ProductMockFactory.createProductWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductServiceTest {

    private static final String BLANK_STRING = "   ";
    private static final String CATEGORY_ID_WITH_SPACES = "  " + DEFAULT_CATEGORY_ID + "  ";

    private static final String EMPTY_SKU_EXCEPTION_MESSAGE = "O SKU do produto é obrigatório para criação.";
    private static final String INVALID_CATEGORY_EXCEPTION_MESSAGE = "É necessário informar uma categoria válida para o produto.";
    private static final String DUPLICATE_SKU_EXCEPTION_MESSAGE = "SKU já cadastrado: " + DEFAULT_SKU;
    private static final String NOT_FOUND_CATEGORY_EXCEPTION_MESSAGE = "Categoria não encontrada para o ID: " + DEFAULT_CATEGORY_ID;

    private static final String FAILURE_COUNTER_NAME = "business.product.creation.failures";
    private static final String SUCCESS_COUNTER_NAME = "business.product.created.total";

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private GetCategoryUseCase getCategoryUseCase;

    private MeterRegistry meterRegistry;
    private CreateProductService createProductService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        createProductService = new CreateProductService(productRepositoryPort, getCategoryUseCase, meterRegistry);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        Product inputProduct = createProductWithoutId();
        Category category = createCategoryWithId();
        Product savedProduct = createProductWithId();

        when(productRepositoryPort.existsBySku(DEFAULT_SKU)).thenReturn(false);
        when(getCategoryUseCase.findById(DEFAULT_CATEGORY_ID)).thenReturn(category);
        when(productRepositoryPort.saveProduct(any(Product.class))).thenReturn(savedProduct);

        Product result = createProductService.execute(inputProduct);

        assertNotNull(result);
        assertEquals(savedProduct.getId(), result.getId());
        assertEquals(DEFAULT_SKU, result.getSku());
        assertSuccessMetricIncremented(category.getName());
        verify(productRepositoryPort).existsBySku(DEFAULT_SKU);
        verify(getCategoryUseCase).findById(DEFAULT_CATEGORY_ID);
        verify(productRepositoryPort).saveProduct(any(Product.class));
    }

    @Test
    void shouldFormatSkuAndCategoryAndCreateProductSuccessfully() {
        Category category = createCategoryWithId();
        Category categoryWithSpaces = new Category();
        categoryWithSpaces.setId(CATEGORY_ID_WITH_SPACES);

        Product inputProduct = createProduct(null, DEFAULT_LOWERCASE_SKU, categoryWithSpaces);
        Product savedProduct = createProductWithId();

        when(productRepositoryPort.existsBySku(DEFAULT_SKU)).thenReturn(false);
        when(getCategoryUseCase.findById(DEFAULT_CATEGORY_ID)).thenReturn(category);
        when(productRepositoryPort.saveProduct(any(Product.class))).thenReturn(savedProduct);

        Product result = createProductService.execute(inputProduct);

        assertNotNull(result);
        assertEquals(DEFAULT_SKU, result.getSku());
        verify(productRepositoryPort).existsBySku(DEFAULT_SKU);
        verify(getCategoryUseCase).findById(DEFAULT_CATEGORY_ID);
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenSkuIsNull() {
        Product inputProduct = createProduct(null, null, createCategoryWithId());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> createProductService.execute(inputProduct)
        );

        assertEquals(EMPTY_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("empty_sku");
        verifyNoRepositoryOrCategoryInteractions();
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenSkuIsBlank() {
        Product inputProduct = createProduct(null, BLANK_STRING, createCategoryWithId());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> createProductService.execute(inputProduct)
        );

        assertEquals(EMPTY_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("empty_sku");
        verifyNoRepositoryOrCategoryInteractions();
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenCategoryIsNull() {
        Product inputProduct = createProduct(null, DEFAULT_SKU, null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> createProductService.execute(inputProduct)
        );

        assertEquals(INVALID_CATEGORY_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("invalid_category");
        verifyNoRepositoryOrCategoryInteractions();
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenCategoryIdIsNull() {
        Product inputProduct = createProduct(null, DEFAULT_SKU, new Category());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> createProductService.execute(inputProduct)
        );

        assertEquals(INVALID_CATEGORY_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("invalid_category");
        verifyNoRepositoryOrCategoryInteractions();
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenSkuAlreadyExists() {
        Product inputProduct = createProductWithoutId();

        when(productRepositoryPort.existsBySku(DEFAULT_SKU)).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> createProductService.execute(inputProduct)
        );

        assertEquals(DUPLICATE_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("duplicate_sku");
        verify(productRepositoryPort).existsBySku(DEFAULT_SKU);
        verify(getCategoryUseCase, never()).findById(anyString());
        verify(productRepositoryPort, never()).saveProduct(any());
    }

    @Test
    void shouldPropagateExceptionWhenCategoryNotFound() {
        Product inputProduct = createProductWithoutId();

        when(productRepositoryPort.existsBySku(DEFAULT_SKU)).thenReturn(false);
        when(getCategoryUseCase.findById(DEFAULT_CATEGORY_ID))
                .thenThrow(new ResourceNotFoundException(NOT_FOUND_CATEGORY_EXCEPTION_MESSAGE));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> createProductService.execute(inputProduct)
        );

        assertEquals(NOT_FOUND_CATEGORY_EXCEPTION_MESSAGE, exception.getMessage());
        verify(productRepositoryPort).existsBySku(DEFAULT_SKU);
        verify(getCategoryUseCase).findById(DEFAULT_CATEGORY_ID);
        verify(productRepositoryPort, never()).saveProduct(any());
    }

    private void verifyNoRepositoryOrCategoryInteractions() {
        verify(productRepositoryPort, never()).existsBySku(anyString());
        verify(getCategoryUseCase, never()).findById(anyString());
        verify(productRepositoryPort, never()).saveProduct(any());
    }

    private void assertFailureMetricIncremented(String reason) {
        double count = meterRegistry.counter(
                FAILURE_COUNTER_NAME,
                "layer", "usecase",
                "reason", reason
        ).count();

        assertEquals(1.0, count);
    }

    private void assertSuccessMetricIncremented(String categoryName) {
        double count = meterRegistry.counter(
                SUCCESS_COUNTER_NAME,
                "category_name", categoryName
        ).count();

        assertEquals(1.0, count);
    }
}