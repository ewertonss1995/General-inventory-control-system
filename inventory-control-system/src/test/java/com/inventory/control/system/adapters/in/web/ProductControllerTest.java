package com.inventory.control.system.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.control.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.system.adapters.in.web.dto.response.UpdateStockResponse;
import com.inventory.control.system.adapters.in.web.mapper.ProductMapper;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import com.inventory.control.system.domain.model.Category;
import com.inventory.control.system.domain.model.Product;
import com.inventory.control.system.domain.model.UpdateStockInput;
import com.inventory.control.system.domain.model.enums.StockMovementType;
import com.inventory.control.system.ports.in.product.CreateProductUseCase;
import com.inventory.control.system.ports.in.product.GetProductUseCase;
import com.inventory.control.system.ports.in.product.UpdateProductUseCase;
import com.inventory.control.system.ports.in.product.UpdateStockUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@WithMockUser
class ProductControllerTest {

    private static final String BASE_URL = "/v1/products";
    private static final String PRODUCT_ID = "prod-123";
    private static final String SKU = "PROD-ABC-001";
    private static final String NAME = "Teclado Mecânico";
    private static final String DESCRIPTION = "Teclado RGB Switch Blue";
    private static final BigDecimal PRICE = new BigDecimal("299.90");
    private static final Integer QUANTITY = 10;
    private static final String CATEGORY_ID = "cat-123";
    private static final String CATEGORY_NAME = "Periféricos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductMapper productMapper;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @MockBean
    private GetProductUseCase getProductUseCase;

    @MockBean
    private UpdateStockUseCase updateStockUseCase;

    @MockBean
    private UpdateProductUseCase updateProductUseCase;

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        ProductRequest request = new ProductRequest(SKU, NAME, DESCRIPTION, PRICE, QUANTITY, CATEGORY_ID);
        Product productDomain = createDummyProduct();
        SaveProductResponse response = new SaveProductResponse(
                PRODUCT_ID, SKU, NAME, DESCRIPTION, PRICE, QUANTITY, CATEGORY_NAME
        );

        when(productMapper.toProduct(any(ProductRequest.class))).thenReturn(productDomain);
        when(createProductUseCase.execute(productDomain)).thenReturn(productDomain);
        when(productMapper.toSaveProductResponse(productDomain)).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                .andExpect(jsonPath("$.sku").value(SKU))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.price").value(299.90))
                .andExpect(jsonPath("$.quantity").value(QUANTITY))
                .andExpect(jsonPath("$.categoryName").value(CATEGORY_NAME));

        verify(productMapper).toProduct(request);
        verify(createProductUseCase).execute(productDomain);
        verify(productMapper).toSaveProductResponse(productDomain);
    }

    @Test
    void shouldReturnBadRequestWhenCreateProductRequestIsInvalid() throws Exception {
        ProductRequest invalidRequest = new ProductRequest(null, "", null, null, null, null);

        mockMvc.perform(post(BASE_URL + "/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(createProductUseCase, never()).execute(any());
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        ProductRequest request = new ProductRequest(SKU, NAME, DESCRIPTION, PRICE, QUANTITY, CATEGORY_ID);
        Product productDomain = createDummyProduct();
        SaveProductResponse response = new SaveProductResponse(
                PRODUCT_ID, SKU, NAME, DESCRIPTION, PRICE, QUANTITY, CATEGORY_NAME
        );

        when(productMapper.toProduct(any(ProductRequest.class))).thenReturn(productDomain);
        when(updateProductUseCase.execute(eq(SKU), any(Product.class))).thenReturn(productDomain);
        when(productMapper.toSaveProductResponse(productDomain)).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/update/{sku}", SKU)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                .andExpect(jsonPath("$.sku").value(SKU))
                .andExpect(jsonPath("$.categoryName").value(CATEGORY_NAME));

        verify(productMapper).toProduct(request);
        verify(updateProductUseCase).execute(SKU, productDomain);
        verify(productMapper).toSaveProductResponse(productDomain);
    }

    @Test
    void shouldGetAllProductsSuccessfully() throws Exception {
        Product productDomain = createDummyProduct();
        CategoryResponse categoryResponse = new CategoryResponse(CATEGORY_ID, CATEGORY_NAME, "Teclados e mouses");
        ProductResponse response = new ProductResponse(
                PRODUCT_ID, SKU, NAME, DESCRIPTION, PRICE, QUANTITY, categoryResponse
        );

        when(getProductUseCase.findAll()).thenReturn(List.of(productDomain));
        when(productMapper.toProductResponseList(List.of(productDomain))).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sku").value(SKU))
                .andExpect(jsonPath("$[0].category.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$[0].category.name").value(CATEGORY_NAME));

        verify(getProductUseCase).findAll();
        verify(productMapper).toProductResponseList(List.of(productDomain));
    }

    @Test
    void shouldGetProductBySkuSuccessfully() throws Exception {
        Product productDomain = createDummyProduct();
        CategoryResponse categoryResponse = new CategoryResponse(CATEGORY_ID, CATEGORY_NAME, "Teclados e mouses");
        ProductResponse response = new ProductResponse(
                PRODUCT_ID, SKU, NAME, DESCRIPTION, PRICE, QUANTITY, categoryResponse
        );

        when(getProductUseCase.findBySku(SKU)).thenReturn(productDomain);
        when(productMapper.toProductResponse(productDomain)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/{sku}", SKU)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                .andExpect(jsonPath("$.sku").value(SKU))
                .andExpect(jsonPath("$.category.name").value(CATEGORY_NAME));

        verify(getProductUseCase).findBySku(SKU);
        verify(productMapper).toProductResponse(productDomain);
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        when(getProductUseCase.findBySku(SKU)).thenThrow(new ResourceNotFoundException("Produto não encontrado."));

        mockMvc.perform(get(BASE_URL + "/{sku}", SKU)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(getProductUseCase).findBySku(SKU);
    }

    @Test
    void shouldUpdateProductStockSuccessfully() throws Exception {
        UpdateStockRequest request = new UpdateStockRequest(5, StockMovementType.IN);
        UpdateStockInput stockInput = new UpdateStockInput(5, StockMovementType.IN);
        Product updatedProduct = createDummyProduct();
        UpdateStockResponse response = new UpdateStockResponse(
                SKU, 10, 15, "IN", "Estoque atualizado com sucesso"
        );

        when(productMapper.toUpdateStockInput(any(UpdateStockRequest.class))).thenReturn(stockInput);
        when(updateStockUseCase.execute(eq(SKU), any(UpdateStockInput.class))).thenReturn(updatedProduct);
        when(productMapper.toUpdateStockResponse(request, updatedProduct)).thenReturn(response);

        mockMvc.perform(patch(BASE_URL + "/{sku}/stock", SKU)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value(SKU))
                .andExpect(jsonPath("$.previousQuantity").value(10))
                .andExpect(jsonPath("$.newQuantity").value(15))
                .andExpect(jsonPath("$.movementType").value("IN"))
                .andExpect(jsonPath("$.message").value("Estoque atualizado com sucesso"));

        verify(productMapper).toUpdateStockInput(request);
        verify(updateStockUseCase).execute(SKU, stockInput);
        verify(productMapper).toUpdateStockResponse(request, updatedProduct);
    }

    @Test
    void shouldReturnBadRequestWhenUpdateStockRequestIsInvalid() throws Exception {
        UpdateStockRequest invalidRequest = new UpdateStockRequest(null, null);

        mockMvc.perform(patch(BASE_URL + "/{sku}/stock", SKU)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(updateStockUseCase, never()).execute(any(), any());
    }

    private Product createDummyProduct() {
        return new Product(
                PRODUCT_ID,
                SKU,
                NAME,
                DESCRIPTION,
                PRICE,
                QUANTITY,
                new Category(CATEGORY_ID, CATEGORY_NAME, null)
        );
    }
}