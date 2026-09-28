package com.inventory.control.system.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.system.adapters.in.web.mapper.CategoryMapper;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.ports.in.category.CreateCategoryUseCase;
import com.inventory.control.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.system.ports.in.category.UpdateCategoryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@WithMockUser // 👈 Resolve os erros 401/403 criando uma sessão autenticada simulada
class CategoryControllerTest {

    private static final String BASE_URL = "/v1/categories";
    private static final String CATEGORY_ID = "cat-123";
    private static final String CATEGORY_NAME = "Eletrônicos";
    private static final String CATEGORY_DESCRIPTION = "Dispositivos e acessórios eletrônicos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryMapper categoryMapper;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @MockBean
    private GetCategoryUseCase getCategoryUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @Test
    void shouldCreateCategorySuccessfully() throws Exception {
        CategoryRequest request = new CategoryRequest(CATEGORY_NAME, CATEGORY_DESCRIPTION);
        Category categoryDomain = new Category(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);
        CategoryResponse response = new CategoryResponse(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);

        when(categoryMapper.toCategory(any(CategoryRequest.class))).thenReturn(categoryDomain);
        when(createCategoryUseCase.execute(categoryDomain)).thenReturn(categoryDomain);
        when(categoryMapper.toCategoryResponse(categoryDomain)).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(CATEGORY_DESCRIPTION));

        verify(categoryMapper).toCategory(request);
        verify(createCategoryUseCase).execute(categoryDomain);
        verify(categoryMapper).toCategoryResponse(categoryDomain);
    }

    @Test
    void shouldReturnBadRequestWhenCategoryNameIsNull() throws Exception {
        CategoryRequest invalidRequest = new CategoryRequest(null, CATEGORY_DESCRIPTION);

        mockMvc.perform(post(BASE_URL + "/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(createCategoryUseCase, never()).execute(any());
    }

    @Test
    void shouldUpdateCategorySuccessfully() throws Exception {
        CategoryRequest request = new CategoryRequest(CATEGORY_NAME, CATEGORY_DESCRIPTION);
        Category categoryDomain = new Category(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);
        CategoryResponse response = new CategoryResponse(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);

        when(categoryMapper.toCategory(any(CategoryRequest.class))).thenReturn(categoryDomain);
        when(updateCategoryUseCase.execute(eq(CATEGORY_ID), any(Category.class))).thenReturn(categoryDomain);
        when(categoryMapper.toCategoryResponse(categoryDomain)).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/update/{id}", CATEGORY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(CATEGORY_DESCRIPTION));

        verify(categoryMapper).toCategory(request);
        verify(updateCategoryUseCase).execute(CATEGORY_ID, categoryDomain);
        verify(categoryMapper).toCategoryResponse(categoryDomain);
    }

    @Test
    void shouldReturnBadRequestWhenUpdateCategoryNameIsNull() throws Exception {
        CategoryRequest invalidRequest = new CategoryRequest(null, CATEGORY_DESCRIPTION);

        mockMvc.perform(put(BASE_URL + "/update/{id}", CATEGORY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(updateCategoryUseCase, never()).execute(any(), any());
    }

    @Test
    void shouldGetAllCategoriesSuccessfully() throws Exception {
        Category categoryDomain = new Category(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);
        CategoryResponse response = new CategoryResponse(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);

        when(getCategoryUseCase.findAll()).thenReturn(List.of(categoryDomain));
        when(categoryMapper.toCategoryResponseList(List.of(categoryDomain))).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(CATEGORY_ID))
                .andExpect(jsonPath("$[0].name").value(CATEGORY_NAME))
                .andExpect(jsonPath("$[0].description").value(CATEGORY_DESCRIPTION));

        verify(getCategoryUseCase).findAll();
        verify(categoryMapper).toCategoryResponseList(List.of(categoryDomain));
    }

    @Test
    void shouldGetCategoryByIdSuccessfully() throws Exception {
        Category categoryDomain = new Category(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);
        CategoryResponse response = new CategoryResponse(CATEGORY_ID, CATEGORY_NAME, CATEGORY_DESCRIPTION);

        when(getCategoryUseCase.findById(CATEGORY_ID)).thenReturn(categoryDomain);
        when(categoryMapper.toCategoryResponse(categoryDomain)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{id}", CATEGORY_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(CATEGORY_NAME))
                .andExpect(jsonPath("$.description").value(CATEGORY_DESCRIPTION));

        verify(getCategoryUseCase).findById(CATEGORY_ID);
        verify(categoryMapper).toCategoryResponse(categoryDomain);
    }

    @Test
    void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
        when(getCategoryUseCase.findById(CATEGORY_ID)).thenThrow(new ResourceNotFoundException("Categoria não encontrada."));

        mockMvc.perform(get(BASE_URL + "/{id}", CATEGORY_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(getCategoryUseCase).findById(CATEGORY_ID);
    }
}