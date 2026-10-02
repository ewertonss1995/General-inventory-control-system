package com.inventory.control.web.system.domain.service.category;

import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
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

import static com.inventory.control.web.system.mocks.domain.service.category.CategoryDomainMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.web.system.mocks.domain.service.category.CategoryDomainMockFactory.createValidCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCategoryServiceTest {

    @Mock
    private CategoryFeignPort categoryFeignPort;

    @InjectMocks
    private GetCategoryService getCategoryService;

    @Test
    void shouldFindAllCategoriesSuccessfully() {
        Category expectedCategory = createValidCategory();
        List<Category> expectedList = List.of(expectedCategory);

        when(categoryFeignPort.findAll()).thenReturn(expectedList);

        List<Category> result = getCategoryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedCategory.getId(), result.get(0).getId());
        assertEquals(expectedCategory.getName(), result.get(0).getName());

        verify(categoryFeignPort).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoCategoriesFound() {
        when(categoryFeignPort.findAll()).thenReturn(Collections.emptyList());

        List<Category> result = getCategoryService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(categoryFeignPort).findAll();
    }

    @Test
    void shouldFindByIdSuccessfullyAndTrimId() {
        String unformattedId = "  " + DEFAULT_CATEGORY_ID + "  ";
        Category expectedCategory = createValidCategory();

        when(categoryFeignPort.findById(DEFAULT_CATEGORY_ID)).thenReturn(expectedCategory);

        Category result = getCategoryService.findById(unformattedId);

        assertNotNull(result);
        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());

        verify(categoryFeignPort).findById(DEFAULT_CATEGORY_ID);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowBusinessExceptionWhenFindByIdWithInvalidId(String invalidId) {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> getCategoryService.findById(invalidId)
        );

        assertEquals("O ID informado para busca de categoria não pode ser nulo.", exception.getMessage());
        verifyNoInteractions(categoryFeignPort);
    }
}