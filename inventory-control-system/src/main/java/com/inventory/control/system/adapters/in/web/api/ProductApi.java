package com.inventory.control.system.adapters.in.web.api;

import com.inventory.control.system.adapters.in.web.dto.request.ProductRequest;
import com.inventory.control.system.adapters.in.web.dto.request.UpdateStockRequest;
import com.inventory.control.system.adapters.in.web.dto.response.ErrorResponse;
import com.inventory.control.system.adapters.in.web.dto.response.ProductResponse;
import com.inventory.control.system.adapters.in.web.dto.response.SaveProductResponse;
import com.inventory.control.system.adapters.in.web.dto.response.UpdateStockResponse;
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

@Tag(name = "Produtos Core", description = "Serviço interno de gestão de produtos e movimentação de estoque")
@SecurityRequirement(name = "bearerAuth")
public interface ProductApi {

    @Operation(
        summary = "Cadastra um novo produto",
        description = "Persiste um novo produto no banco de dados e valida o relacionamento com a categoria informada."
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
            description = "Falha de validação ou violação de regra de negócio ao cadastrar produto",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ValidacaoProduto",
                    summary = "Erro de validação de payload",
                    value = """
                        {
                          "status": 400,
                          "title": "Erro de Validação de Payload",
                          "message": "Um ou mais campos do formulário/requisição estão inválidos.",
                          "timestamp": "2026-10-07T11:26:00Z",
                          "errors": [
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
            description = "Categoria informada no ID não existe",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "CategoriaNaoEncontrada",
                    summary = "Categoria não cadastrada",
                    value = """
                        {
                          "status": 404,
                          "title": "Recurso Não Encontrado",
                          "message": "Categoria não encontrada para o ID '60d5ec49f1b2c82b1c8e4567'",
                          "timestamp": "2026-10-07T11:26:00Z",
                          "errors": []
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<SaveProductResponse> createProduct(@RequestBody @Valid ProductRequest request);

    @Operation(
        summary = "Atualiza dados de um produto existente",
        description = "Altera as informações do produto referente ao SKU especificado."
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
            description = "Parâmetros inválidos ou erro de regra de negócio",
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
            @Parameter(description = "SKU único do produto a ser atualizado", example = "PRD-TECL-001")
            @PathVariable String sku,
            @RequestBody @Valid ProductRequest request);

    @Operation(
        summary = "Consulta a lista de produtos cadastrados",
        description = "Retorna todos os produtos do catálogo com os detalhes completos da categoria vinculada."
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
        summary = "Obtém detalhes do produto por SKU",
        description = "Busca as informações detalhadas do produto identificado pelo SKU informado."
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
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "ProdutoNaoEncontrado",
                    summary = "Produto não existe",
                    value = """
                        {
                          "status": 404,
                          "title": "Recurso Não Encontrado",
                          "message": "Produto com SKU 'PRD-INEXISTENTE' não foi encontrado.",
                          "timestamp": "2026-10-07T11:26:00Z",
                          "errors": []
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<ProductResponse> getProductBySku(
            @Parameter(description = "SKU do produto buscado", example = "PRD-TECL-001")
            @PathVariable String sku);

    @Operation(
        summary = "Atualiza o estoque de um produto",
        description = "Processa uma movimentação de entrada (IN) ou saída (OUT) no saldo em estoque."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estoque atualizado com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UpdateStockResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Erro de validação ou saldo insuficiente em estoque para dar baixa",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "SaldoInsuficiente",
                    summary = "Violacao de regra de negocio - estoque insuficiente",
                    value = """
                        {
                          "status": 400,
                          "title": "Regra de Negócio Violada",
                          "message": "Saldo insuficiente para o produto SKU 'PRD-TECL-001'. Saldo atual: 5, Quantidade solicitada: 10",
                          "timestamp": "2026-10-07T11:26:00Z",
                          "errors": []
                        }
                        """
                )
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
            @Parameter(description = "SKU do produto que terá o estoque alterado", example = "PRD-TECL-001")
            @PathVariable String sku,
            @RequestBody @Valid UpdateStockRequest request);
}