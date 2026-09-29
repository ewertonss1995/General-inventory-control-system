package com.inventory.control.web.system.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.UpdateStockResponse;
import com.inventory.control.web.system.adapters.in.web.mapper.ProductMapper;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.domain.model.UpdateStock;
import com.inventory.control.web.system.domain.model.UpdateStockInput;
import com.inventory.control.web.system.ports.in.product.GetProductUseCase;
import com.inventory.control.web.system.ports.in.product.PostProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.web.system.ports.in.product.UpdateStockUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.DEFAULT_CATEGORY_NAME;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.DEFAULT_PRODUCT_ID;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.DEFAULT_SKU;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.createProductDomain;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.createProductResponse;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.createSaveProductResponse;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.createUpdateStockDomain;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.createUpdateStockResponse;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.createValidProductRequest;
import static com.inventory.control.web.system.adapters.in.web.ProductWebMockFactory.createValidUpdateStockRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ProductController.class)
@WithMockUser
class ProductControllerTest {

    private static final String BASE_URL = "/api/v1/products";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductMapper mapper;

    @MockBean
    private GetProductUseCase getProductUseCase;

    @MockBean
    private PostProductUseCase postProductUseCase;

    @MockBean
    private UpdateProductUseCase updateProductUseCase;

    @MockBean
    private UpdateStockUseCase updateStockUseCase;

    @Test
    void shouldCreateProductAndReturn201Created() throws Exception {
        ProductRequest request = createValidProductRequest();
        Product productDomain = createProductDomain();
        SaveProductResponse response = createSaveProductResponse();

        when(mapper.toProduct(any(ProductRequest.class))).thenReturn(productDomain);
        when(postProductUseCase.execute(any(Product.class))).thenReturn(productDomain);
        when(mapper.toSaveProductResponse(any(Product.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(DEFAULT_PRODUCT_ID))
                .andExpect(jsonPath("$.sku").value(DEFAULT_SKU))
                .andExpect(jsonPath("$.categoryName").value(DEFAULT_CATEGORY_NAME));

        verify(postProductUseCase).execute(any(Product.class));
    }

    @Test
    void shouldReturn400BadRequestWhenCreateProductRequestIsInvalid() throws Exception {
        ProductRequest invalidRequest = new ProductRequest("", "", "Description", new BigDecimal("-1.0"), -1, null);

        mockMvc.perform(post(BASE_URL + "/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(postProductUseCase, never()).execute(any());
    }

    @Test
    void shouldUpdateProductAndReturn200Ok() throws Exception {
        ProductRequest request = createValidProductRequest();
        Product productDomain = createProductDomain();
        SaveProductResponse response = createSaveProductResponse();

        when(mapper.toProduct(any(ProductRequest.class))).thenReturn(productDomain);
        when(updateProductUseCase.execute(eq(DEFAULT_SKU), any(Product.class))).thenReturn(productDomain);
        when(mapper.toSaveProductResponse(any(Product.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/update/{sku}", DEFAULT_SKU)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(DEFAULT_SKU));

        verify(updateProductUseCase).execute(eq(DEFAULT_SKU), any(Product.class));
    }

    @Test
    void shouldReturn400BadRequestWhenUpdateProductRequestIsInvalid() throws Exception {
        ProductRequest invalidRequest = new ProductRequest(null, null, null, null, null, null);

        mockMvc.perform(put(BASE_URL + "/update/{sku}", DEFAULT_SKU)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(updateProductUseCase, never()).execute(anyString(), any());
    }

    @Test
    void shouldGetAllProductsAndReturn200Ok() throws Exception {
        Product productDomain = createProductDomain();
        ProductResponse productResponse = createProductResponse();

        when(getProductUseCase.findAll()).thenReturn(List.of(productDomain));
        when(mapper.toProductResponseList(any())).thenReturn(List.of(productResponse));

        mockMvc.perform(get(BASE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sku").value(DEFAULT_SKU));

        verify(getProductUseCase).findAll();
    }

    @Test
    void shouldGetProductBySkuAndReturn200Ok() throws Exception {
        Product productDomain = createProductDomain();
        ProductResponse productResponse = createProductResponse();

        when(getProductUseCase.findBySku(DEFAULT_SKU)).thenReturn(productDomain);
        when(mapper.toProductResponse(productDomain)).thenReturn(productResponse);

        mockMvc.perform(get(BASE_URL + "/{sku}", DEFAULT_SKU)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(DEFAULT_SKU));

        verify(getProductUseCase).findBySku(DEFAULT_SKU);
    }

    @Test
    void shouldUpdateProductStockAndReturn200Ok() throws Exception {
        UpdateStockRequest request = createValidUpdateStockRequest();
        UpdateStock updateStockDomain = createUpdateStockDomain();
        UpdateStockResponse response = createUpdateStockResponse();

        when(mapper.toUpdateStockRequest(any(UpdateStockRequest.class))).thenReturn(new UpdateStockInput(5, "IN"));
        when(updateStockUseCase.execute(eq(DEFAULT_SKU), any())).thenReturn(updateStockDomain);
        when(mapper.toUpdateStockResponse(updateStockDomain)).thenReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{sku}/stock", DEFAULT_SKU)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(DEFAULT_SKU))
                .andExpect(jsonPath("$.newQuantity").value(20));

        verify(updateStockUseCase).execute(eq(DEFAULT_SKU), any());
    }

    @Test
    void shouldReturn400BadRequestWhenUpdateStockRequestIsInvalid() throws Exception {
        UpdateStockRequest invalidRequest = new UpdateStockRequest(0, "");

        mockMvc.perform(patch(BASE_URL + "/{sku}/stock", DEFAULT_SKU)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(updateStockUseCase, never()).execute(anyString(), any());
    }
}