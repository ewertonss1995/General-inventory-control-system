package com.inventory.control.web.system.adapters.in.web.api;

import com.inventory.control.web.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.web.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.web.system.adapters.in.web.dto.response.ErrorResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.web.system.adapters.in.web.dto.response.UpdateStockResponse;
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

@Tag(name = "Gestão de Produtos", description = "Endpoints para cadastro, atualização, consulta e movimentação de estoque de produtos")
@SecurityRequirement(name = "bearerAuth")
public interface ProductApi {

    @Operation(
        summary = "Cadastra um novo produto",
        description = "Cria um novo produto no catálogo associando-o a uma categoria existente."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Produto criado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SaveProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro de validação nos dados do produto",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ValidacaoProduto",
                    summary = "Exemplo de erro de validação",
                    value = """
                        {
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Erro na validação dos campos informados.",
                          "path": "/api/v1/products/save",
                          "timestamp": "2026-10-07T11:45:00Z",
                          "fieldErrors": [
                            {
                              "field": "price",
                              "message": "Preço é obrigatório"
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Categoria informada não existe",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Conflito (SKU já cadastrado no sistema)",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<SaveProductResponse> createProduct(@RequestBody @Valid ProductRequest request);

    @Operation(
        summary = "Atualiza os dados de um produto",
        description = "Atualiza as informações cadastrais de um produto localizado pelo SKU."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Produto atualizado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SaveProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro nos dados de alteração enviados",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Produto com o SKU informado não foi encontrado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<SaveProductResponse> updateProduct(
            @Parameter(description = "SKU do produto a ser atualizado", example = "PRD-TECL-001")
            @PathVariable String sku,
            @RequestBody @Valid ProductRequest request);

    @Operation(
        summary = "Lista todos os produtos",
        description = "Retorna a listagem completa dos produtos cadastrados no catálogo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de produtos retornada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))
            )
        )
    })
    ResponseEntity<List<ProductResponse>> getAllProducts();

    @Operation(
        summary = "Busca produto por SKU",
        description = "Retorna os detalhes completos de um produto específico através de seu código SKU."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Produto localizado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Produto não encontrado para o SKU informado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<ProductResponse> getProductBySku(
            @Parameter(description = "SKU único do produto", example = "PRD-TECL-001")
            @PathVariable String sku);

    @Operation(
        summary = "Atualiza o saldo de estoque do produto",
        description = "Executa uma entrada (IN) ou saída (OUT) no saldo em estoque de um produto."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Movimentação de estoque realizada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UpdateStockResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Tipo de movimentação inválido ou quantidade insuficiente em estoque para saída",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Produto não localizado",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<UpdateStockResponse> updateProductStock(
            @Parameter(description = "SKU do produto que sofrerá alteração de estoque", example = "PRD-TECL-001")
            @PathVariable String sku,
            @RequestBody @Valid UpdateStockRequest request);
}