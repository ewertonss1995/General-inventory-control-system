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

import java.util.Optional;

import static com.inventory.control.system.mocks.CategoryMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithId;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_LOWERCASE_SKU;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_PRODUCT_ID;
import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_QUANTITY;
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
class UpdateProductServiceTest {

    private static final String BLANK_STRING = "   ";
    private static final String SKU_WITH_SPACES = "  " + DEFAULT_SKU + "  ";

    private static final String EMPTY_SKU_EXCEPTION_MESSAGE = "O SKU do produto é obrigatório para atualização.";
    private static final String INVALID_CATEGORY_EXCEPTION_MESSAGE = "É necessário informar uma categoria válida para atualizar o produto.";
    private static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Produto não encontrado para o SKU: " + DEFAULT_SKU;
    private static final String CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE = "Categoria não encontrada para o ID: " + DEFAULT_CATEGORY_ID;

    private static final String FAILURE_COUNTER_NAME = "business.product.update.failures";
    private static final String SUCCESS_COUNTER_NAME = "business.product.updated.total";

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private GetCategoryUseCase getCategoryUseCase;

    private MeterRegistry meterRegistry;
    private UpdateProductService updateProductService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        updateProductService = new UpdateProductService(productRepositoryPort, getCategoryUseCase, meterRegistry);
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product existingProduct = createProductWithId();
        Category category = createCategoryWithId();
        Product updateRequest = createProductWithoutId();
        Product updatedProduct = createProductWithId();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(existingProduct));
        when(getCategoryUseCase.findById(DEFAULT_CATEGORY_ID)).thenReturn(category);
        when(productRepositoryPort.updateProduct(any(Product.class))).thenReturn(updatedProduct);

        Product result = updateProductService.execute(DEFAULT_SKU, updateRequest);

        assertNotNull(result);
        assertEquals(DEFAULT_PRODUCT_ID, result.getId());
        assertEquals(DEFAULT_SKU, result.getSku());
        assertSuccessMetricIncremented(category.getName());
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
        verify(getCategoryUseCase).findById(DEFAULT_CATEGORY_ID);
        verify(productRepositoryPort).updateProduct(any(Product.class));
    }

    @Test
    void shouldFormatSkuAndUpdateProductSuccessfullyWhenSkuHasSpacesOrLowerCase() {
        Product existingProduct = createProductWithId();
        Category category = createCategoryWithId();
        Product updateRequest = createProductWithoutId();
        Product updatedProduct = createProductWithId();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(existingProduct));
        when(getCategoryUseCase.findById(DEFAULT_CATEGORY_ID)).thenReturn(category);
        when(productRepositoryPort.updateProduct(any(Product.class))).thenReturn(updatedProduct);

        Product result = updateProductService.execute(SKU_WITH_SPACES, updateRequest);

        assertNotNull(result);
        assertEquals(DEFAULT_SKU, result.getSku());
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
        verify(getCategoryUseCase).findById(DEFAULT_CATEGORY_ID);
        verify(productRepositoryPort).updateProduct(any(Product.class));
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenSkuIsNull() {
        Product updateRequest = createProductWithoutId();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateProductService.execute(null, updateRequest)
        );

        assertEquals(EMPTY_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("empty_sku");
        verifyNoInteractionsWithDependencies();
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenSkuIsBlank() {
        Product updateRequest = createProductWithoutId();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateProductService.execute(BLANK_STRING, updateRequest)
        );

        assertEquals(EMPTY_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("empty_sku");
        verifyNoInteractionsWithDependencies();
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenCategoryIsNull() {
        Product updateRequest = createProduct(null, DEFAULT_SKU, null, DEFAULT_QUANTITY);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateProductService.execute(DEFAULT_SKU, updateRequest)
        );

        assertEquals(INVALID_CATEGORY_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("invalid_category");
        verifyNoInteractionsWithDependencies();
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenCategoryIdIsNull() {
        Product updateRequest = createProduct(null, DEFAULT_SKU, new Category(), DEFAULT_QUANTITY);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateProductService.execute(DEFAULT_SKU, updateRequest)
        );

        assertEquals(INVALID_CATEGORY_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("invalid_category");
        verifyNoInteractionsWithDependencies();
    }

    @Test
    void shouldThrowResourceNotFoundExceptionAndIncrementMetricWhenProductNotFound() {
        Product updateRequest = createProductWithoutId();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateProductService.execute(DEFAULT_SKU, updateRequest)
        );

        assertEquals(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("product_not_found");
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
        verify(getCategoryUseCase, never()).findById(anyString());
        verify(productRepositoryPort, never()).updateProduct(any());
    }

    @Test
    void shouldPropagateResourceNotFoundExceptionWhenCategoryNotFound() {
        Product existingProduct = createProductWithId();
        Product updateRequest = createProductWithoutId();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(existingProduct));
        when(getCategoryUseCase.findById(DEFAULT_CATEGORY_ID))
                .thenThrow(new ResourceNotFoundException(CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateProductService.execute(DEFAULT_SKU, updateRequest)
        );

        assertEquals(CATEGORY_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
        verify(getCategoryUseCase).findById(DEFAULT_CATEGORY_ID);
        verify(productRepositoryPort, never()).updateProduct(any());
    }

    private void verifyNoInteractionsWithDependencies() {
        verify(productRepositoryPort, never()).findBySku(anyString());
        verify(getCategoryUseCase, never()).findById(anyString());
        verify(productRepositoryPort, never()).updateProduct(any());
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