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
class PostProductServiceTest {

    @Mock
    private ProductFeignPort productFeignPort;

    @InjectMocks
    private PostProductService postProductService;

    @Test
    void shouldCreateProductSuccessfully() {
        Product inputProduct = createValidProduct();
        Product expectedProduct = createValidProduct();

        when(productFeignPort.saveProduct(inputProduct)).thenReturn(expectedProduct);

        Product result = postProductService.execute(inputProduct);

        assertNotNull(result);
        assertEquals(expectedProduct.getId(), result.getId());
        assertEquals(expectedProduct.getSku(), result.getSku());
        assertEquals(expectedProduct.getName(), result.getName());
        assertEquals(expectedProduct.getCategory().getId(), result.getCategory().getId());

        verify(productFeignPort).saveProduct(inputProduct);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowBusinessExceptionWhenSkuIsInvalid(String invalidSku) {
        Product product = createValidProduct();
        product.setSku(invalidSku);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> postProductService.execute(product)
        );

        assertEquals("O SKU do produto é obrigatório para criação.", exception.getMessage());
        verifyNoInteractions(productFeignPort);
    }

    @Test
    void shouldThrowBusinessExceptionWhenCategoryIsNull() {
        Product product = createProductWithoutCategory();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> postProductService.execute(product)
        );

        assertEquals("É necessário informar uma categoria válida para o produto.", exception.getMessage());
        verifyNoInteractions(productFeignPort);
    }

    @Test
    void shouldThrowBusinessExceptionWhenCategoryIdIsNull() {
        Product product = createProductWithCategoryWithoutId();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> postProductService.execute(product)
        );

        assertEquals("É necessário informar uma categoria válida para o produto.", exception.getMessage());
        verifyNoInteractions(productFeignPort);
    }
}