package com.inventory.control.system.domain.service.product;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.enums.StockMovementType;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.UpdateStockInput;
import com.inventory.control.system.ports.out.ProductRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.inventory.control.system.mocks.ProductMockFactory.DEFAULT_SKU;
import static com.inventory.control.system.mocks.ProductMockFactory.createProductWithId;
import static com.inventory.control.system.mocks.StockMockFactory.createCustomMovementInput;
import static com.inventory.control.system.mocks.StockMockFactory.createInMovementInput;
import static com.inventory.control.system.mocks.StockMockFactory.createOutMovementInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateStockServiceTest {

    private static final String BLANK_STRING = "   ";
    private static final String SKU_WITH_SPACES = "  " + DEFAULT_SKU + "  ";

    private static final String INVALID_SKU_EXCEPTION_MESSAGE = "SKU não pode ser nulo ou vazio.";
    private static final String PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE = "Produto não encontrado para o SKU: " + DEFAULT_SKU;

    private static final String FAILURE_COUNTER_NAME = "business.stock.update.failures";
    private static final String OPERATIONS_COUNTER_NAME = "business.stock.movement.operations.total";
    private static final String ITEMS_COUNTER_NAME = "business.stock.movement.items";

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    private MeterRegistry meterRegistry;
    private UpdateStockService updateStockService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        updateStockService = new UpdateStockService(productRepositoryPort, meterRegistry);
    }

    @Test
    void shouldAddStockSuccessfullyWhenMovementTypeIsIN() {
        Product existingProduct = createProductWithId();
        int initialQuantity = existingProduct.getQuantity();
        UpdateStockInput input = createInMovementInput();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(existingProduct));
        when(productRepositoryPort.updateProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = updateStockService.execute(DEFAULT_SKU, input);

        assertNotNull(result);
        assertEquals(initialQuantity + input.quantity(), result.getQuantity());
        assertOperationsMetricIncremented("IN");
        assertItemsMetricIncremented("IN", input.quantity());
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
        verify(productRepositoryPort).updateProduct(existingProduct);
    }

    @Test
    void shouldRemoveStockSuccessfullyWhenMovementTypeIsOUT() {
        Product existingProduct = createProductWithId();
        int initialQuantity = existingProduct.getQuantity();
        UpdateStockInput input = createOutMovementInput();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(existingProduct));
        when(productRepositoryPort.updateProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = updateStockService.execute(DEFAULT_SKU, input);

        assertNotNull(result);
        assertEquals(initialQuantity - input.quantity(), result.getQuantity());
        assertOperationsMetricIncremented("OUT");
        assertItemsMetricIncremented("OUT", input.quantity());
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
        verify(productRepositoryPort).updateProduct(existingProduct);
    }

    @Test
    void shouldFormatSkuAndAddStockSuccessfully() {
        Product existingProduct = createProductWithId();
        UpdateStockInput input = createInMovementInput();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(existingProduct));
        when(productRepositoryPort.updateProduct(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = updateStockService.execute(SKU_WITH_SPACES, input);

        assertNotNull(result);
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionAndIncrementMetricWhenSkuIsNull() {
        UpdateStockInput input = createInMovementInput();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateStockService.execute(null, input)
        );

        assertEquals(INVALID_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("invalid_sku");
        verify(productRepositoryPort, never()).findBySku(anyString());
        verify(productRepositoryPort, never()).updateProduct(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionAndIncrementMetricWhenSkuIsBlank() {
        UpdateStockInput input = createInMovementInput();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateStockService.execute(BLANK_STRING, input)
        );

        assertEquals(INVALID_SKU_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("invalid_sku");
        verify(productRepositoryPort, never()).findBySku(anyString());
        verify(productRepositoryPort, never()).updateProduct(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionAndIncrementMetricWhenProductNotFound() {
        UpdateStockInput input = createInMovementInput();

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateStockService.execute(DEFAULT_SKU, input)
        );

        assertEquals(PRODUCT_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("product_not_found");
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
        verify(productRepositoryPort, never()).updateProduct(any());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionAndIncrementMetricWhenInQuantityIsInvalid() {
        Product existingProduct = createProductWithId();
        UpdateStockInput input = createCustomMovementInput(StockMovementType.IN, 0);

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(existingProduct));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateStockService.execute(DEFAULT_SKU, input)
        );

        assertEquals("A quantidade para entrada de estoque deve ser maior que zero.", exception.getMessage());
        assertFailureMetricIncremented("business_rule_violation");
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
        verify(productRepositoryPort, never()).updateProduct(any());
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenOutQuantityExceedsStock() {
        Product existingProduct = createProductWithId();
        UpdateStockInput input = createCustomMovementInput(StockMovementType.OUT, 100);

        when(productRepositoryPort.findBySku(DEFAULT_SKU)).thenReturn(Optional.of(existingProduct));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateStockService.execute(DEFAULT_SKU, input)
        );

        assertEquals(
                String.format("Saldo insuficiente para o produto SKU '%s'. Saldo atual: 15, Quantidade solicitada: 100", DEFAULT_SKU),
                exception.getMessage()
        );
        assertFailureMetricIncremented("business_rule_violation");
        verify(productRepositoryPort).findBySku(DEFAULT_SKU);
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

    private void assertOperationsMetricIncremented(String type) {
        double count = meterRegistry.counter(
                OPERATIONS_COUNTER_NAME,
                "type", type
        ).count();

        assertEquals(1.0, count);
    }

    private void assertItemsMetricIncremented(String type, double expectedAmount) {
        double count = meterRegistry.counter(
                ITEMS_COUNTER_NAME,
                "type", type
        ).count();

        assertEquals(expectedAmount, count);
    }
}