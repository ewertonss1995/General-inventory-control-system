package com.inventory.control.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resposta do cadastro ou atualização de produto")
public record SaveProductResponse(
    @Schema(description = "Identificador do produto", example = "60d5ec49f1b2c82b1c8e4999")
    String id,

    @Schema(description = "SKU do produto", example = "PRD-TECL-001")
    String sku,

    @Schema(description = "Nome do produto", example = "Teclado Mecânico RGB")
    String name,

    @Schema(description = "Descrição técnica", example = "Teclado mecânico ABNT2")
    String description,

    @Schema(description = "Preço", example = "299.90")
    BigDecimal price,

    @Schema(description = "Quantidade em estoque", example = "50")
    Integer quantity,

    @Schema(description = "Nome da categoria associada", example = "Periféricos")
    String categoryName
) {}