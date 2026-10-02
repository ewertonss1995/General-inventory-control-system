package com.inventory.control.web.system.domain.service.product;

import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.out.ProductFeignPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.DEFAULT_SKU;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createValidProduct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductServiceTest {

    @Mock
    private ProductFeignPort productFeignPort;

    @InjectMocks
    private GetProductService getProductService;

    @Test
    void shouldFindAllProductsSuccessfully() {
        Product expectedProduct = createValidProduct();
        List<Product> expectedList = List.of(expectedProduct);

        when(productFeignPort.findAll()).thenReturn(expectedList);

        List<Product> result = getProductService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedProduct.getSku(), result.get(0).getSku());

        verify(productFeignPort).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsFound() {
        when(productFeignPort.findAll()).thenReturn(Collections.emptyList());

        List<Product> result = getProductService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(productFeignPort).findAll();
    }

    @Test
    void shouldFindBySkuSuccessfullyAndFormatSku() {
        String unformattedSku = "  sku-prod-001  ";
        Product expectedProduct = createValidProduct();

        when(productFeignPort.findBySku(DEFAULT_SKU)).thenReturn(expectedProduct);

        Product result = getProductService.findBySku(unformattedSku);

        assertNotNull(result);
        assertEquals(expectedProduct.getSku(), result.getSku());
        assertEquals(expectedProduct.getName(), result.getName());

        verify(productFeignPort).findBySku(DEFAULT_SKU);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowBusinessExceptionWhenFindBySkuWithInvalidSku(String invalidSku) {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> getProductService.findBySku(invalidSku)
        );

        assertEquals("O SKU informado para busca não pode ser nulo ou vazio.", exception.getMessage());
        verifyNoInteractions(productFeignPort);
    }
}