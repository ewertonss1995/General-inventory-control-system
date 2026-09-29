package com.inventory.control.web.system.adapters.out.client;

import com.inventory.control.web.system.adapters.out.client.dto.request.InventoryCategoryRequest;
import com.inventory.control.web.system.adapters.out.client.dto.response.InventoryCategoryResponse;
import com.inventory.control.web.system.adapters.out.mapper.InventoryCategoryMapper;
import com.inventory.control.web.system.domain.model.Category;
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

import static com.inventory.control.web.system.adapters.out.client.CategoryClientMockFactory.DEFAULT_ID;
import static com.inventory.control.web.system.adapters.out.client.CategoryClientMockFactory.createCategoryDomain;
import static com.inventory.control.web.system.adapters.out.client.CategoryClientMockFactory.createCategoryDomainList;
import static com.inventory.control.web.system.adapters.out.client.CategoryClientMockFactory.createCategoryDomainWithoutId;
import static com.inventory.control.web.system.adapters.out.client.CategoryClientMockFactory.createInventoryCategoryRequest;
import static com.inventory.control.web.system.adapters.out.client.CategoryClientMockFactory.createInventoryCategoryResponse;
import static com.inventory.control.web.system.adapters.out.client.CategoryClientMockFactory.createInventoryCategoryResponseList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryClientAdapterTest {

    @Mock
    private InventoryCategoryMapper mapper;

    @Mock
    private InventoryFeignClient inventoryFeignClient;

    private MeterRegistry meterRegistry;

    private CategoryClientAdapter categoryClientAdapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        categoryClientAdapter = new CategoryClientAdapter(mapper, inventoryFeignClient, meterRegistry);
    }

    @Test
    void shouldSaveCategorySuccessfully() {
        Category inputCategory = createCategoryDomainWithoutId();
        Category expectedCategory = createCategoryDomain();
        InventoryCategoryRequest request = createInventoryCategoryRequest();
        InventoryCategoryResponse response = createInventoryCategoryResponse();

        when(mapper.categoryToInventoryCategoryRequest(inputCategory)).thenReturn(request);
        when(inventoryFeignClient.createCategory(request)).thenReturn(ResponseEntity.ok(response));
        when(mapper.toCategory(response)).thenReturn(expectedCategory);

        Category result = categoryClientAdapter.saveCategory(inputCategory);

        assertNotNull(result);
        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());
        assertEquals(expectedCategory.getDescription(), result.getDescription());

        verify(mapper).categoryToInventoryCategoryRequest(inputCategory);
        verify(inventoryFeignClient).createCategory(request);
        verify(mapper).toCategory(response);

        // Valida que a métrica do Timer foi registrada
        assertEquals(1, meterRegistry.find("client.feign.category.time")
                .tag("operation", "saveCategory").timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenSaveCategoryFails() {
        Category inputCategory = createCategoryDomainWithoutId();
        InventoryCategoryRequest request = createInventoryCategoryRequest();

        when(mapper.categoryToInventoryCategoryRequest(inputCategory)).thenReturn(request);
        when(inventoryFeignClient.createCategory(request)).thenThrow(new RuntimeException("Feign communication error"));

        assertThrows(RuntimeException.class, () -> categoryClientAdapter.saveCategory(inputCategory));

        // Valida registro de contador de falhas no Micrometer
        assertEquals(1.0, meterRegistry.find("client.feign.category.failures")
                .tag("operation", "saveCategory").counter().count());
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Category inputCategory = createCategoryDomain();
        Category expectedCategory = createCategoryDomain();
        InventoryCategoryRequest request = createInventoryCategoryRequest();
        InventoryCategoryResponse response = createInventoryCategoryResponse();

        when(mapper.categoryToInventoryCategoryRequest(inputCategory)).thenReturn(request);
        when(inventoryFeignClient.updateCategory(DEFAULT_ID, request)).thenReturn(ResponseEntity.ok(response));
        when(mapper.toCategory(response)).thenReturn(expectedCategory);

        Category result = categoryClientAdapter.updateCategory(DEFAULT_ID, inputCategory);

        assertNotNull(result);
        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());

        verify(mapper).categoryToInventoryCategoryRequest(inputCategory);
        verify(inventoryFeignClient).updateCategory(DEFAULT_ID, request);
        verify(mapper).toCategory(response);

        assertEquals(1, meterRegistry.find("client.feign.category.time")
                .tag("operation", "updateCategory").timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenUpdateCategoryFails() {
        Category inputCategory = createCategoryDomain();
        InventoryCategoryRequest request = createInventoryCategoryRequest();

        when(mapper.categoryToInventoryCategoryRequest(inputCategory)).thenReturn(request);
        when(inventoryFeignClient.updateCategory(DEFAULT_ID, request)).thenThrow(new RuntimeException("Error updating category"));

        assertThrows(RuntimeException.class, () -> categoryClientAdapter.updateCategory(DEFAULT_ID, inputCategory));

        assertEquals(1.0, meterRegistry.find("client.feign.category.failures")
                .tag("operation", "updateCategory").counter().count());
    }

    @Test
    void shouldFindAllCategoriesSuccessfullyAndRecordSummaryMetric() {
        List<InventoryCategoryResponse> responseList = createInventoryCategoryResponseList();
        List<Category> expectedList = createCategoryDomainList();

        when(inventoryFeignClient.getAllCategories()).thenReturn(ResponseEntity.ok(responseList));
        when(mapper.toCategoryList(responseList)).thenReturn(expectedList);

        List<Category> result = categoryClientAdapter.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(inventoryFeignClient).getAllCategories();
        verify(mapper).toCategoryList(responseList);

        // Valida registro do tamanho do resultado na métrica de summary
        assertEquals(1, meterRegistry.find("client.feign.category.findall.result.size").summary().count());
        assertEquals(2.0, meterRegistry.find("client.feign.category.findall.result.size").summary().totalAmount());
    }

    @Test
    void shouldFindAllCategoriesWithEmptyBody() {
        when(inventoryFeignClient.getAllCategories()).thenReturn(ResponseEntity.ok(null));
        when(mapper.toCategoryList(null)).thenReturn(Collections.emptyList());

        List<Category> result = categoryClientAdapter.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(inventoryFeignClient).getAllCategories();
        verify(mapper).toCategoryList(null);
    }

    @Test
    void shouldRecordFailureMetricWhenFindAllFails() {
        when(inventoryFeignClient.getAllCategories()).thenThrow(new RuntimeException("Error fetching categories"));

        assertThrows(RuntimeException.class, () -> categoryClientAdapter.findAll());

        assertEquals(1.0, meterRegistry.find("client.feign.category.failures")
                .tag("operation", "findAll").counter().count());
    }

    @Test
    void shouldFindCategoryByIdSuccessfully() {
        Category expectedCategory = createCategoryDomain();
        InventoryCategoryResponse response = createInventoryCategoryResponse();

        when(inventoryFeignClient.getCategoryById(DEFAULT_ID)).thenReturn(ResponseEntity.ok(response));
        when(mapper.toCategory(response)).thenReturn(expectedCategory);

        Category result = categoryClientAdapter.findById(DEFAULT_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_ID, result.getId());
        assertEquals(expectedCategory.getName(), result.getName());

        verify(inventoryFeignClient).getCategoryById(DEFAULT_ID);
        verify(mapper).toCategory(response);

        assertEquals(1, meterRegistry.find("client.feign.category.time")
                .tag("operation", "findById").timer().count());
    }

    @Test
    void shouldRecordFailureMetricWhenFindByIdFails() {
        when(inventoryFeignClient.getCategoryById(DEFAULT_ID)).thenThrow(new RuntimeException("Category not found"));

        assertThrows(RuntimeException.class, () -> categoryClientAdapter.findById(DEFAULT_ID));

        assertEquals(1.0, meterRegistry.find("client.feign.category.failures")
                .tag("operation", "findById").counter().count());
    }
}