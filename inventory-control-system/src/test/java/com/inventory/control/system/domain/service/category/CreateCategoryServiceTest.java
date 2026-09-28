package com.inventory.control.system.domain.service.category;

import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.out.CategoryRepositoryPort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.inventory.control.system.mocks.CategoryMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithId;
import static com.inventory.control.system.mocks.CategoryMockFactory.createCategoryWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCategoryServiceTest {

    private static final String EXPECTED_EXCEPTION_MESSAGE = "ID de categoria já cadastrado: " + DEFAULT_CATEGORY_ID;
    private static final String FAILURE_COUNTER_NAME = "business.category.failures";

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    private MeterRegistry meterRegistry;
    private CreateCategoryService createCategoryService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        createCategoryService = new CreateCategoryService(categoryRepositoryPort, meterRegistry);
    }

    @Test
    void shouldCreateCategorySuccessfullyWhenIdIsNull() {
        Category inputCategory = createCategoryWithoutId();
        Category savedCategory = createCategoryWithId();

        when(categoryRepositoryPort.saveCategory(inputCategory)).thenReturn(savedCategory);

        Category result = createCategoryService.execute(inputCategory);

        assertNotNull(result);
        assertEquals(savedCategory.getId(), result.getId());
        assertEquals(savedCategory.getName(), result.getName());
        assertEquals(savedCategory.getDescription(), result.getDescription());
        verify(categoryRepositoryPort, never()).existsById(any());
        verify(categoryRepositoryPort).saveCategory(inputCategory);
    }

    @Test
    void shouldCreateCategorySuccessfullyWhenIdDoesNotExist() {
        Category inputCategory = createCategoryWithId();

        when(categoryRepositoryPort.existsById(DEFAULT_CATEGORY_ID)).thenReturn(false);
        when(categoryRepositoryPort.saveCategory(inputCategory)).thenReturn(inputCategory);

        Category result = createCategoryService.execute(inputCategory);

        assertNotNull(result);
        assertEquals(inputCategory.getId(), result.getId());
        verify(categoryRepositoryPort).existsById(DEFAULT_CATEGORY_ID);
        verify(categoryRepositoryPort).saveCategory(inputCategory);
    }

    @Test
    void shouldThrowBusinessExceptionAndIncrementMetricWhenCategoryAlreadyExists() {
        Category inputCategory = createCategoryWithId();

        when(categoryRepositoryPort.existsById(DEFAULT_CATEGORY_ID)).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> createCategoryService.execute(inputCategory)
        );

        assertEquals(EXPECTED_EXCEPTION_MESSAGE, exception.getMessage());
        assertFailureMetricIncremented();
        verify(categoryRepositoryPort).existsById(DEFAULT_CATEGORY_ID);
        verify(categoryRepositoryPort, never()).saveCategory(any());
    }

    private void assertFailureMetricIncremented() {
        double count = meterRegistry.counter(
                FAILURE_COUNTER_NAME,
                "operation", "createCategory",
                "reason", "category_already_exists"
        ).count();

        assertEquals(1.0, count);
    }
}