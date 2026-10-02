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

import java.util.Optional;

import static com.inventory.control.system.mocks.CategoryMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.system.mocks.CategoryMockFactory.SECONDARY_CATEGORY_DESCRIPTION;
import static com.inventory.control.system.mocks.CategoryMockFactory.SECONDARY_CATEGORY_NAME;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategory;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithId;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryServiceTest {

    private static final String ID_WITH_WHITESPACES = "  " + DEFAULT_CATEGORY_ID + "  ";
    private static final String BLANK_ID = "   ";

    private static final String MISSING_ID_EXCEPTION_MESSAGE = "O ID da categoria é obrigatório para atualização.";
    private static final String NOT_FOUND_EXCEPTION_MESSAGE = "Categoria não encontrada para o ID: " + DEFAULT_CATEGORY_ID;

    private static final String UPDATE_OPERATION = "updateCategory";
    private static final String FAILURE_COUNTER_NAME = "business.category.failures";

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    private MeterRegistry meterRegistry;
    private UpdateCategoryService updateCategoryService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        updateCategoryService = new UpdateCategoryService(categoryRepositoryPort, meterRegistry);
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Category existingCategory = createCategoryWithId();
        Category updateRequest = createCategory(null, SECONDARY_CATEGORY_NAME, SECONDARY_CATEGORY_DESCRIPTION);
        Category updatedCategory = createCategory(DEFAULT_CATEGORY_ID, SECONDARY_CATEGORY_NAME, SECONDARY_CATEGORY_DESCRIPTION);

        when(categoryRepositoryPort.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.of(existingCategory));
        when(categoryRepositoryPort.updateCategory(existingCategory)).thenReturn(updatedCategory);

        Category result = updateCategoryService.execute(DEFAULT_CATEGORY_ID, updateRequest);

        assertNotNull(result);
        assertEquals(DEFAULT_CATEGORY_ID, result.getId());
        assertEquals(SECONDARY_CATEGORY_NAME, result.getName());
        assertEquals(SECONDARY_CATEGORY_DESCRIPTION, result.getDescription());
        verify(categoryRepositoryPort).findById(DEFAULT_CATEGORY_ID);
        verify(categoryRepositoryPort).updateCategory(existingCategory);
    }

    @Test
    void shouldUpdateCategorySuccessfullyWhenIdHasWhitespaces() {
        Category existingCategory = createCategoryWithId();
        Category updateRequest = createCategory(null, SECONDARY_CATEGORY_NAME, SECONDARY_CATEGORY_DESCRIPTION);
        Category updatedCategory = createCategory(DEFAULT_CATEGORY_ID, SECONDARY_CATEGORY_NAME, SECONDARY_CATEGORY_DESCRIPTION);

        when(categoryRepositoryPort.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.of(existingCategory));
        when(categoryRepositoryPort.updateCategory(existingCategory)).thenReturn(updatedCategory);

        Category result = updateCategoryService.execute(ID_WITH_WHITESPACES, updateRequest);

        assertNotNull(result);
        assertEquals(DEFAULT_CATEGORY_ID, result.getId());
        verify(categoryRepositoryPort).findById(DEFAULT_CATEGORY_ID);
        verify(categoryRepositoryPort).updateCategory(existingCategory);
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenIdIsNull() {
        Category updateRequest = createCategoryWithoutId();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateCategoryService.execute(null, updateRequest)
        );

        assertEquals(MISSING_ID_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("missing_id");
        verify(categoryRepositoryPort, never()).findById(anyString());
        verify(categoryRepositoryPort, never()).updateCategory(any());
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenIdIsBlank() {
        Category updateRequest = createCategoryWithoutId();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateCategoryService.execute(BLANK_ID, updateRequest)
        );

        assertEquals(MISSING_ID_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("missing_id");
        verify(categoryRepositoryPort, never()).findById(anyString());
        verify(categoryRepositoryPort, never()).updateCategory(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionAndIncrementMetricWhenCategoryDoesNotExist() {
        Category updateRequest = createCategoryWithoutId();

        when(categoryRepositoryPort.findById(DEFAULT_CATEGORY_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateCategoryService.execute(DEFAULT_CATEGORY_ID, updateRequest)
        );

        assertEquals(NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented("category_not_found");
        verify(categoryRepositoryPort).findById(DEFAULT_CATEGORY_ID);
        verify(categoryRepositoryPort, never()).updateCategory(any());
    }

    private void assertFailureMetricIncremented(String reason) {
        double count = meterRegistry.counter(
                FAILURE_COUNTER_NAME,
                "layer", "usecase",
                "operation", UPDATE_OPERATION,
                "reason", reason
        ).count();

        assertEquals(1.0, count);
    }
}