package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.in.web.mapper.InventoryProductMapper;
import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductRequest;
import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryProductStockRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventorySaveProductResponse;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryUpdateStockResponse;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.DEFAULT_SKU;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createInventoryProductResponse;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createInventoryProductResponseList;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createInventoryProductStockRequest;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createInventorySaveProductResponse;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createInventoryUpdateStockResponse;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createProductDomain;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createProductDomainList;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createUpdateStockDomain;
import static com.inventory.control.web.system.adapters.out.client.ProductClientMockFactory.createUpdateStockInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductClientAdapterTest {

    @Mock
    private InventoryProductMapper mapper;

    @Mock
    private InventoryFeignClient inventoryFeignClient;

    private MeterRegistry meterRegistry;

    private ProductClientAdapter productClientAdapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        productClientAdapter = new ProductClientAdapter(mapper, inventoryFeignClient, meterRegistry);
    }

    @Test
    void shouldSaveProductSuccessfully() {
        Product productInput = createProductDomain();
        InventorySaveProductResponse responseBody = createInventorySaveProductResponse();

        when(mapper.toInventoryProductRequest(productInput)).thenReturn(null);
        when(inventoryFeignClient.createProduct(any())).thenReturn(ResponseEntity.ok(responseBody));
        when(mapper.inventorySaveProductResponseToProduct(responseBody)).thenReturn(productInput);

        Product result = productClientAdapter.saveProduct(productInput);

        assertNotNull(result);
        assertEquals(DEFAULT_SKU, result.getSku());

        verify(mapper).toInventoryProductRequest(productInput);
        verify(inventoryFeignClient).createProduct(any());
        verify(mapper).inventorySaveProductResponseToProduct(responseBody);

        assertEquals(1, meterRegistry.find("client.feign.product.time")
                .tag("operation", "saveProduct").timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenSaveProductFails() {
        Product productInput = createProductDomain();

        when(mapper.toInventoryProductRequest(productInput)).thenReturn(null);
        when(inventoryFeignClient.createProduct(any())).thenThrow(new RuntimeException("Error saving product"));

        assertThrows(RuntimeException.class, () -> productClientAdapter.saveProduct(productInput));

        assertEquals(1.0, meterRegistry.find("client.feign.product.failures")
                .tag("operation", "saveProduct").counter().count());
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        Product productInput = createProductDomain();
        InventorySaveProductResponse responseBody = createInventorySaveProductResponse();

        when(mapper.toInventoryProductRequest(productInput)).thenReturn(null);
        when(inventoryFeignClient.updateProduct(eq(DEFAULT_SKU), any())).thenReturn(ResponseEntity.ok(responseBody));
        when(mapper.inventorySaveProductResponseToProduct(responseBody)).thenReturn(productInput);

        Product result = productClientAdapter.updateProduct(DEFAULT_SKU, productInput);

        assertNotNull(result);
        assertEquals(DEFAULT_SKU, result.getSku());

        verify(mapper).toInventoryProductRequest(productInput);
        verify(inventoryFeignClient).updateProduct(eq(DEFAULT_SKU), any());
        verify(mapper).inventorySaveProductResponseToProduct(responseBody);

        assertEquals(1, meterRegistry.find("client.feign.product.time")
                .tag("operation", "updateProduct").timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenUpdateProductFails() {
        Product productInput = createProductDomain();

        when(mapper.toInventoryProductRequest(productInput)).thenReturn(null);
        when(inventoryFeignClient.updateProduct(eq(DEFAULT_SKU), any())).thenThrow(new RuntimeException("Error updating product"));

        assertThrows(RuntimeException.class, () -> productClientAdapter.updateProduct(DEFAULT_SKU, productInput));

        assertEquals(1.0, meterRegistry.find("client.feign.product.failures")
                .tag("operation", "updateProduct").counter().count());
    }

    @Test
    void shouldFindAllProductsSuccessfullyAndRecordSummaryMetric() {
        List<InventoryProductResponse> responseList = createInventoryProductResponseList();
        List<Product> expectedList = createProductDomainList();

        when(inventoryFeignClient.getAllProducts()).thenReturn(ResponseEntity.ok(responseList));
        when(mapper.toProductList(responseList)).thenReturn(expectedList);

        List<Product> result = productClientAdapter.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(inventoryFeignClient).getAllProducts();
        verify(mapper).toProductList(responseList);

        assertEquals(1, meterRegistry.find("client.feign.product.findall.result.size").summary().count());
        assertEquals(1.0, meterRegistry.find("client.feign.product.findall.result.size").summary().totalAmount());
    }

    @Test
    void shouldFindAllProductsWithEmptyBody() {
        when(inventoryFeignClient.getAllProducts()).thenReturn(ResponseEntity.ok(null));
        when(mapper.toProductList(null)).thenReturn(Collections.emptyList());

        List<Product> result = productClientAdapter.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(inventoryFeignClient).getAllProducts();
        verify(mapper).toProductList(null);
    }

    @Test
    void shouldRecordFailureMetricWhenFindAllFails() {
        when(inventoryFeignClient.getAllProducts()).thenThrow(new RuntimeException("Error fetching products"));

        assertThrows(RuntimeException.class, () -> productClientAdapter.findAll());

        assertEquals(1.0, meterRegistry.find("client.feign.product.failures")
                .tag("operation", "findAll").counter().count());
    }

    @Test
    void shouldFindBySkuSuccessfully() {
        Product expectedProduct = createProductDomain();
        InventoryProductResponse responseBody = createInventoryProductResponse();

        when(inventoryFeignClient.getProductBySku(DEFAULT_SKU)).thenReturn(ResponseEntity.ok(responseBody));
        when(mapper.toProduct(responseBody)).thenReturn(expectedProduct);

        Product result = productClientAdapter.findBySku(DEFAULT_SKU);

        assertNotNull(result);
        assertEquals(DEFAULT_SKU, result.getSku());

        verify(inventoryFeignClient).getProductBySku(DEFAULT_SKU);
        verify(mapper).toProduct(responseBody);

        assertEquals(1, meterRegistry.find("client.feign.product.time")
                .tag("operation", "findBySku").timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenFindBySkuFails() {
        when(inventoryFeignClient.getProductBySku(DEFAULT_SKU)).thenThrow(new RuntimeException("Product not found"));

        assertThrows(RuntimeException.class, () -> productClientAdapter.findBySku(DEFAULT_SKU));

        assertEquals(1.0, meterRegistry.find("client.feign.product.failures")
                .tag("operation", "findBySku").counter().count());
    }

    @Test
    void shouldUpdateProductStockSuccessfully() {
        UpdateStockInput input = createUpdateStockInput();
        InventoryProductStockRequest request = createInventoryProductStockRequest();
        InventoryUpdateStockResponse responseBody = createInventoryUpdateStockResponse();
        UpdateStock expectedUpdateStock = createUpdateStockDomain();

        when(mapper.toInventoryProductStockRequest(input)).thenReturn(request);
        when(inventoryFeignClient.updateProductStock(DEFAULT_SKU, request)).thenReturn(ResponseEntity.ok(responseBody));
        when(mapper.toUpdateStock(DEFAULT_SKU, responseBody)).thenReturn(expectedUpdateStock);

        UpdateStock result = productClientAdapter.updateProductStock(DEFAULT_SKU, input);

        assertNotNull(result);
        assertEquals(DEFAULT_SKU, result.getSku());
        assertEquals(60, result.getNewQuantity());

        verify(mapper).toInventoryProductStockRequest(input);
        verify(inventoryFeignClient).updateProductStock(DEFAULT_SKU, request);
        verify(mapper).toUpdateStock(DEFAULT_SKU, responseBody);

        assertEquals(1, meterRegistry.find("client.feign.product.time")
                .tag("operation", "updateProductStock").timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenUpdateProductStockFails() {
        UpdateStockInput input = createUpdateStockInput();
        InventoryProductStockRequest request = createInventoryProductStockRequest();

        when(mapper.toInventoryProductStockRequest(input)).thenReturn(request);
        when(inventoryFeignClient.updateProductStock(DEFAULT_SKU, request)).thenThrow(new RuntimeException("Error updating stock"));

        assertThrows(RuntimeException.class, () -> productClientAdapter.updateProductStock(DEFAULT_SKU, input));

        assertEquals(1.0, meterRegistry.find("client.feign.product.failures")
                .tag("operation", "updateProductStock").counter().count());
    }
}