package com.inventory.control.system.domain.service.category;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.inventory.control.system.mocks.CategoryMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.system.mocks.CategoryMockFactory.SECONDARY_CATEGORY_NAME;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategory;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCategoryServiceTest {

    private static final String ID_WITH_WHITESPACES = "  " + DEFAULT_CATEGORY_ID + "  ";
    private static final String BLANK_ID = "   ";

    private static final String BUSINESS_EXCEPTION_MESSAGE = "O ID informado para busca de categoria não pode ser nulo.";
    private static final String RESOURCE_NOT_FOUND_EXCEPTION_MESSAGE = "Categoria não encontrada para o ID: " + DEFAULT_CATEGORY_ID;

    private static final String FIND_BY_ID_OPERATION = "findById";
    private static final String FAILURE_COUNTER_NAME = "business.category.failures";
    private static final String SUMMARY_METRIC_NAME = "usecase.category.findall.result.size";

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    private MeterRegistry meterRegistry;
    private GetCategoryService getCategoryService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        getCategoryService = new GetCategoryService(categoryRepositoryPort, meterRegistry);
    }

    @Test
    void shouldFindAllCategoriesSuccessfully() {
        Category categoryOne = createCategoryWithId();
        Category categoryTwo = createCategory("66b4f1a2e3b0c44298fc1c15", SECONDARY_CATEGORY_NAME, "Description");
        List<Category> expectedCategories = List.of(categoryOne, categoryTwo);

        when(categoryRepositoryPort.findAll()).thenReturn(expectedCategories);

        List<Category> result = getCategoryService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(2.0, meterRegistry.summary(SUMMARY_METRIC_NAME).max());
        verify(categoryRepositoryPort).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoCategoriesExist() {
        when(categoryRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        List<Category> result = getCategoryService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0.0, meterRegistry.summary(SUMMARY_METRIC_NAME).max());
        verify(categoryRepositoryPort).findAll();
    }

    @Test
    void shouldFindCategoryByIdSuccessfully() {
        Category expectedCategory = createCategoryWithId();

        when(categoryRepositoryPort.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.of(expectedCategory));

        Category result = getCategoryService.findById(DEFAULT_CATEGORY_ID);

        assertNotNull(result);
        assertEquals(DEFAULT_CATEGORY_ID, result.getId());
        assertEquals(expectedCategory.getName(), result.getName());
        verify(categoryRepositoryPort).findById(DEFAULT_CATEGORY_ID);
    }

    @Test
    void shouldFindCategoryByIdWhenIdHasWhitespaces() {
        Category expectedCategory = createCategoryWithId();

        when(categoryRepositoryPort.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.of(expectedCategory));

        Category result = getCategoryService.findById(ID_WITH_WHITESPACES);

        assertNotNull(result);
        assertEquals(DEFAULT_CATEGORY_ID, result.getId());
        verify(categoryRepositoryPort).findById(DEFAULT_CATEGORY_ID);
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenIdIsNull() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> getCategoryService.findById(null)
        );

        assertEquals(BUSINESS_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("missing_id");
        verify(categoryRepositoryPort, never()).findById(anyString());
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenIdIsBlank() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> getCategoryService.findById(BLANK_ID)
        );

        assertEquals(BUSINESS_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("missing_id");
        verify(categoryRepositoryPort, never()).findById(anyString());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionAndIncrementMetricWhenCategoryDoesNotExist() {
        when(categoryRepositoryPort.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> getCategoryService.findById(DEFAULT_CATEGORY_ID)
        );

        assertEquals(RESOURCE_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("category_not_found");
        verify(categoryRepositoryPort).findById(DEFAULT_CATEGORY_ID);
    }

    private void assertFailureMetricIncremented(String reason) {
        double count = meterRegistry.counter(
                FAILURE_COUNTER_NAME,
                "layer", "usecase",
                "operation", FIND_BY_ID_OPERATION,
                "reason", reason
        ).count();

        assertEquals(1.0, count);
    }
}