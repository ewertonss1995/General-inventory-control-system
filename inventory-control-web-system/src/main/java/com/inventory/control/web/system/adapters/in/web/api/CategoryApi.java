package com.inventory.control.web.system.adapters.in.web.api;

import com.inventory.control.web.system.adapters.in.web.dto.request.CategoryRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.CategoryResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Gestão de Categorias", description = "Endpoints para cadastro, atualização e consulta do catálogo de categorias de produtos")
@SecurityRequirement(name = "bearerAuth")
public interface CategoryApi {

    @Operation(
        summary = "Cadastra uma nova categoria",
        description = "Cria uma nova categoria no sistema e invalida o cache de listagem de categorias."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Categoria criada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CategoryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro de validação nos dados da categoria",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ValidacaoCategoria",
                    summary = "Exemplo de erro de validação",
                    value = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Erro na validação dos campos informados.",
                          "path": "/api/v1/categories/save",
                          "timestamp": "2026-10-07T11:50:00Z",
                          "fieldErrors": [
                            {
                              "field": "name",
                              "message": "Nome da categoria é obrigatório"
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflito (Categoria com este nome já existe)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request);

    @Operation(
        summary = "Atualiza uma categoria existente",
        description = "Atualiza as informações de uma categoria a partir do seu ID e invalida o cache."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categoria atualizada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CategoryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro de validação nos dados informados",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Categoria não encontrada para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Identificador único da categoria", example = "60d5ec49f1b2c82b1c8e4567")
            @PathVariable String id,
            @RequestBody @Valid CategoryRequest request);

    @Operation(
        summary = "Lista todas as categorias",
        description = "Retorna a listagem completa de categorias cadastradas. Utiliza cache em memória para otimização de performance."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de categorias retornada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = CategoryResponse.class))
            )
        )
    })
    ResponseEntity<List<CategoryResponse>> getAllCategories();

    @Operation(
        summary = "Busca categoria por ID",
        description = "Retorna os detalhes de uma categoria específica através do seu identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Categoria localizada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CategoryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Categoria não encontrada para o ID informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "Identificador único da categoria", example = "60d5ec49f1b2c82b1c8e4567")
            @PathVariable String id);
}