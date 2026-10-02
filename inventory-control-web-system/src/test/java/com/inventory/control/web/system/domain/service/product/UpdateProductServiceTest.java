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

import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.DEFAULT_SKU;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createProductWithCategoryWithoutId;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createProductWithoutCategory;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createValidProduct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {

    @Mock
    private ProductFeignPort productFeignPort;

    @InjectMocks
    private UpdateProductService updateProductService;

    @Test
    void shouldUpdateProductSuccessfully() {
        String sku = DEFAULT_SKU;
        Product inputProduct = createValidProduct();
        Product expectedProduct = createValidProduct();

        when(productFeignPort.updateProduct(sku, inputProduct)).thenReturn(expectedProduct);

        Product result = updateProductService.execute(sku, inputProduct);

        assertNotNull(result);
        assertEquals(expectedProduct.getId(), result.getId());
        assertEquals(expectedProduct.getSku(), result.getSku());
        assertEquals(expectedProduct.getName(), result.getName());
        assertEquals(expectedProduct.getCategory().getId(), result.getCategory().getId());

        verify(productFeignPort).updateProduct(sku, inputProduct);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowBusinessExceptionWhenSkuIsInvalid(String invalidSku) {
        Product inputProduct = createValidProduct();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateProductService.execute(invalidSku, inputProduct)
        );

        assertEquals("O SKU do produto é obrigatório para atualização.", exception.getMessage());
        verifyNoInteractions(productFeignPort);
    }

    @Test
    void shouldThrowBusinessExceptionWhenCategoryIsNull() {
        String sku = DEFAULT_SKU;
        Product inputProduct = createProductWithoutCategory();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateProductService.execute(sku, inputProduct)
        );

        assertEquals("É necessário informar uma categoria válida para atualizar o produto.", exception.getMessage());
        verifyNoInteractions(productFeignPort);
    }

    @Test
    void shouldThrowBusinessExceptionWhenCategoryIdIsNull() {
        String sku = DEFAULT_SKU;
        Product inputProduct = createProductWithCategoryWithoutId();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateProductService.execute(sku, inputProduct)
        );

        assertEquals("É necessário informar uma categoria válida para atualizar o produto.", exception.getMessage());
        verifyNoInteractions(productFeignPort);
    }
}