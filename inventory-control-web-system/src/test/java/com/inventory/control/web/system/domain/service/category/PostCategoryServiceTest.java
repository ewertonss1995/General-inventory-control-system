package com.inventory.control.web.system.domain.service.category;

import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.out.CategoryFeignPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.inventory.control.web.system.mocks.domain.service.category.CategoryDomainMockFactory.createCategoryInput;
import static com.inventory.control.web.system.mocks.domain.service.category.CategoryDomainMockFactory.createValidCategory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCategoryServiceTest {

    @Mock
    private CategoryFeignPort categoryFeignPort;

    @InjectMocks
    private PostCategoryService postCategoryService;

    @Test
    void shouldCreateCategorySuccessfully() {
        Category inputCategory = createCategoryInput();
        Category expectedCategory = createValidCategory();

        when(categoryFeignPort.saveCategory(inputCategory)).thenReturn(expectedCategory);

        Category result = postCategoryService.execute(inputCategory);

        assertNotNull(result);
        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());
        assertEquals(expectedCategory.getDescription(), result.getDescription());

        verify(categoryFeignPort).saveCategory(inputCategory);
    }
}