package com.inventory.control.web.system.domain.service.kafka;

import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.ProductMessage;
import com.inventory.control.web.system.ports.out.EventPublisherPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.DEFAULT_SKU;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.DEFAULT_NAME;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.DEFAULT_DESCRIPTION;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.DEFAULT_PRICE;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.DEFAULT_QUANTITY;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.CATEGORY_ID;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.CATEGORY_NAME;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.CATEGORY_DESC;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createValidProduct;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createProductWithoutSku;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createProductWithBlankSku;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createProductWithoutCategory;
import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createProductWithCategoryWithoutId;

@ExtendWith(MockitoExtension.class)
class ProductCsvProducerServiceTest {

    @Mock
    private EventPublisherPort eventPublisher;

    @InjectMocks
    private ProductCsvProducerService productCsvProducerService;

    private static final String TOPIC_NAME = "inventory-csv-import-topic";

    @Test
    void shouldPublishProductsSuccessfully() {
        productCsvProducerService.publishProducts(List.of(createValidProduct()));

        ArgumentCaptor<ProductMessage> payloadCaptor = ArgumentCaptor.forClass(ProductMessage.class);
        verify(eventPublisher).publish(eq(TOPIC_NAME), eq(DEFAULT_SKU), payloadCaptor.capture());

        ProductMessage capturedMessage = payloadCaptor.getValue();
        assertNotNull(capturedMessage);
        assertEquals(DEFAULT_SKU, capturedMessage.getSku());
        assertEquals(DEFAULT_NAME, capturedMessage.getName());
        assertEquals(DEFAULT_DESCRIPTION, capturedMessage.getDescription());
        assertEquals(DEFAULT_PRICE, capturedMessage.getPrice());
        assertEquals(DEFAULT_QUANTITY, capturedMessage.getQuantity());
        assertEquals(CATEGORY_ID, capturedMessage.getCategoryId());
        assertEquals(CATEGORY_NAME, capturedMessage.getCategoryName());
        assertEquals(CATEGORY_DESC, capturedMessage.getCategoryDescription());
    }

    @Test
    void shouldThrowExceptionWhenSkuIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productCsvProducerService.publishProducts(List.of(createProductWithoutSku()))
        );

        assertEquals("O SKU do produto é obrigatório para criação.", exception.getMessage());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldThrowExceptionWhenSkuIsBlank() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productCsvProducerService.publishProducts(List.of(createProductWithBlankSku()))
        );

        assertEquals("O SKU do produto é obrigatório para criação.", exception.getMessage());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productCsvProducerService.publishProducts(List.of(createProductWithoutCategory()))
        );

        assertEquals("É necessário informar uma categoria válida para o produto.", exception.getMessage());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldThrowExceptionWhenCategoryIdIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> productCsvProducerService.publishProducts(List.of(createProductWithCategoryWithoutId()))
        );

        assertEquals("É necessário informar uma categoria válida para o produto.", exception.getMessage());
        verifyNoInteractions(eventPublisher);
    }
}