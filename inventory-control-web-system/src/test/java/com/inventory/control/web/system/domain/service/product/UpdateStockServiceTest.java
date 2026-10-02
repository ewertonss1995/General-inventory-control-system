package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.inventory.control.web.system.mocks.domain.service.product.StockDomainMockFactory.DEFAULT_SKU;
import static com.inventory.control.web.system.mocks.domain.service.product.StockDomainMockFactory.UNFORMATTED_SKU;
import static com.inventory.control.web.system.mocks.domain.service.product.StockDomainMockFactory.createUpdateStock;
import static com.inventory.control.web.system.mocks.domain.service.product.StockDomainMockFactory.createUpdateStockInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateStockServiceTest {

    @Mock
    private ProductFeignPort productFeignPort;

    @InjectMocks
    private UpdateStockService updateStockService;

    @Test
    void shouldUpdateStockSuccessfullyAndFormatSku() {
        UpdateStockInput input = createUpdateStockInput();
        UpdateStock expectedResponse = createUpdateStock();

        when(productFeignPort.updateProductStock(DEFAULT_SKU, input)).thenReturn(expectedResponse);

        UpdateStock result = updateStockService.execute(UNFORMATTED_SKU, input);

        assertNotNull(result);
        assertEquals(expectedResponse.getSku(), result.getSku());
        assertEquals(expectedResponse.getPreviousQuantity(), result.getPreviousQuantity());
        assertEquals(expectedResponse.getNewQuantity(), result.getNewQuantity());
        assertEquals(expectedResponse.getMovementType(), result.getMovementType());
        assertEquals(expectedResponse.getMessage(), result.getMessage());

        verify(productFeignPort).updateProductStock(DEFAULT_SKU, input);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowBusinessExceptionWhenSkuIsInvalid(String invalidSku) {
        UpdateStockInput input = createUpdateStockInput();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateStockService.execute(invalidSku, input)
        );

        assertEquals("SKU não pode ser nulo ou vazio.", exception.getMessage());
        verifyNoInteractions(productFeignPort);
    }
}