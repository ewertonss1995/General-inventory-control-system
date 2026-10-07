package com.inventory.control.system.adapters.in.web.api;

import com.inventory.control.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.system.adapters.in.web.dto.response.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias do catálogo de produtos")
public interface CategoryApi {

@Operation(
        summary = "Cadastra uma nova categoria",
        description = "Cria um novo registro de categoria no sistema. O nome da categoria deve ser único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Categoria criada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dados de requisição inválidos ou falha na validação de campos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "Conflito: Categoria com o mesmo nome já cadastrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Erro interno no servidor",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CategoryResponse> createCategory(CategoryRequest request);

    @Operation(
        summary = "Atualiza uma categoria existente",
        description = "Atualiza as informações da categoria vinculada ao ID especificado na URL."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Categoria atualizada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Requisição malformada ou dados de entrada inválidos",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Categoria não encontrada para o ID informado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Erro interno no servidor",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CategoryResponse> updateCategory(
        @Parameter(description = "Identificador único da categoria", example = "60d5ec49f1b2c82b1c8e4567", required = true)
        String id,
        CategoryRequest request
    );

    @Operation(
        summary = "Lista todas as categorias",
        description = "Retorna a coleção completa de categorias cadastradas na base de dados."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Consulta realizada com sucesso",
            content = @Content(
                mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Erro interno no servidor",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<List<CategoryResponse>> getAllCategories();

    @Operation(
        summary = "Busca uma categoria por ID",
        description = "Retorna os dados detalhados de uma categoria específica com base no seu ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Categoria encontrada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Categoria não encontrada para o ID fornecido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Erro interno no servidor",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    ResponseEntity<CategoryResponse> getCategoryById(
        @Parameter(description = "Identificador único da categoria", example = "60d5ec49f1b2c82b1c8e4567", required = true)
        String id
    );
}