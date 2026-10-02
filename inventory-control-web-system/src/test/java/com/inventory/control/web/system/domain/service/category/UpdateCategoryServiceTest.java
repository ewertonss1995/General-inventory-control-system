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

import static com.inventory.control.web.system.mocks.domain.service.category.CategoryDomainMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.web.system.mocks.domain.service.category.CategoryDomainMockFactory.createCategoryInput;
import static com.inventory.control.web.system.mocks.domain.service.category.CategoryDomainMockFactory.createValidCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryServiceTest {

    @Mock
    private CategoryFeignPort categoryFeignPort;

    @InjectMocks
    private UpdateCategoryService updateCategoryService;

    @Test
    void shouldUpdateCategorySuccessfullyAndTrimId() {
        String unformattedId = "  " + DEFAULT_CATEGORY_ID + "  ";
        Category inputCategory = createCategoryInput();
        Category expectedCategory = createValidCategory();

        when(categoryFeignPort.updateCategory(DEFAULT_CATEGORY_ID, inputCategory)).thenReturn(expectedCategory);

        Category result = updateCategoryService.execute(unformattedId, inputCategory);

        assertNotNull(result);
        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());
        assertEquals(expectedCategory.getDescription(), result.getDescription());

        verify(categoryFeignPort).updateCategory(DEFAULT_CATEGORY_ID, inputCategory);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldThrowBusinessExceptionWhenCategoryIdIsInvalid(String invalidId) {
        Category inputCategory = createCategoryInput();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateCategoryService.execute(invalidId, inputCategory)
        );

        assertEquals("O ID da categoria é obrigatório para atualização.", exception.getMessage());
        verifyNoInteractions(categoryFeignPort);
    }
}