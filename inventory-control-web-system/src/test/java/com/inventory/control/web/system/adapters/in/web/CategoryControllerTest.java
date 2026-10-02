package com.inventory.control.web.system.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.web.system.adapters.in.web.mapper.CategoryMapper;
import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.ports.in.category.GetCategoryUseCase;
import com.inventory.control.web.system.ports.in.category.PostCategoryUseCase;
import com.inventory.control.web.system.ports.in.category.UpdateCategoryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.inventory.control.web.system.mocks.adapters.in.CategoryWebMockFactory.DEFAULT_CATEGORY_ID;
import static com.inventory.control.web.system.mocks.adapters.in.CategoryWebMockFactory.DEFAULT_NAME;
import static com.inventory.control.web.system.mocks.adapters.in.CategoryWebMockFactory.createCategoryDomain;
import static com.inventory.control.web.system.mocks.adapters.in.CategoryWebMockFactory.createCategoryResponse;
import static com.inventory.control.web.system.mocks.adapters.in.CategoryWebMockFactory.createValidCategoryRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
@WithMockUser
class CategoryControllerTest {

    private static final String BASE_URL = "/api/v1/categories";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryMapper mapper;

    @MockBean
    private PostCategoryUseCase postCategoryUseCase;

    @MockBean
    private GetCategoryUseCase getCategoryUseCase;

    @MockBean
    private UpdateCategoryUseCase updateCategoryUseCase;

    @Test
    void shouldCreateCategoryAndReturn201Created() throws Exception {
        CategoryRequest request = createValidCategoryRequest();
        Category categoryDomain = createCategoryDomain();
        CategoryResponse response = createCategoryResponse();

        when(mapper.toCategory(any(CategoryRequest.class))).thenReturn(categoryDomain);
        when(postCategoryUseCase.execute(any(Category.class))).thenReturn(categoryDomain);
        when(mapper.toCategoryResponse(any(Category.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(DEFAULT_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME));

        verify(postCategoryUseCase).execute(any(Category.class));
    }

    @Test
    void shouldReturn400BadRequestWhenCreateCategoryRequestIsInvalid() throws Exception {
        // Garantindo invalidez enviando name como null (supondo que @NotBlank/@NotNull está no DTO)
        CategoryRequest invalidRequest = new CategoryRequest(null, "Descrição qualquer");

        mockMvc.perform(post(BASE_URL + "/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(postCategoryUseCase, never()).execute(any());
    }

    @Test
    void shouldUpdateCategoryAndReturn200Ok() throws Exception {
        CategoryRequest request = createValidCategoryRequest();
        Category categoryDomain = createCategoryDomain();
        CategoryResponse response = createCategoryResponse();

        when(mapper.toCategory(any(CategoryRequest.class))).thenReturn(categoryDomain);
        when(updateCategoryUseCase.execute(eq(DEFAULT_CATEGORY_ID), any(Category.class))).thenReturn(categoryDomain);
        when(mapper.toCategoryResponse(any(Category.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/update/{id}", DEFAULT_CATEGORY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DEFAULT_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME));

        verify(updateCategoryUseCase).execute(eq(DEFAULT_CATEGORY_ID), any(Category.class));
    }

    @Test
    void shouldReturn400BadRequestWhenUpdateCategoryRequestIsInvalid() throws Exception {
        // Envia null para garantir violação de Bean Validation
        CategoryRequest invalidRequest = new CategoryRequest(null, null);

        mockMvc.perform(put(BASE_URL + "/update/{id}", DEFAULT_CATEGORY_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(updateCategoryUseCase, never()).execute(anyString(), any());
    }

    @Test
    void shouldGetAllCategoriesAndReturn200Ok() throws Exception {
        Category categoryDomain = createCategoryDomain();
        CategoryResponse categoryResponse = createCategoryResponse();

        when(getCategoryUseCase.findAll()).thenReturn(List.of(categoryDomain));
        when(mapper.toCategoryResponseList(any())).thenReturn(List.of(categoryResponse));

        mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(DEFAULT_CATEGORY_ID))
                .andExpect(jsonPath("$[0].name").value(DEFAULT_NAME));

        verify(getCategoryUseCase).findAll();
    }

    @Test
    void shouldGetCategoryByIdAndReturn200Ok() throws Exception {
        Category categoryDomain = createCategoryDomain();
        CategoryResponse categoryResponse = createCategoryResponse();

        when(getCategoryUseCase.findById(DEFAULT_CATEGORY_ID)).thenReturn(categoryDomain);
        when(mapper.toCategoryResponse(categoryDomain)).thenReturn(categoryResponse);

        mockMvc.perform(get(BASE_URL + "/{id}", DEFAULT_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DEFAULT_CATEGORY_ID))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME));

        verify(getCategoryUseCase).findById(DEFAULT_CATEGORY_ID);
    }
}