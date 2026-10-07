package com.inventory.control.web.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resposta simplificada após salvamento/edição do produto")
public record SaveProductResponse(
    @Schema(description = "Identificador único do produto", example = "60d5ec49f1b2c82b1c8e4999")
    String id,

    @Schema(description = "SKU do produto", example = "PRD-TECL-001")
    String sku,

    @Schema(description = "Nome do produto", example = "Teclado Mecânico RGB")
    String name,

    @Schema(description = "Descrição detalhada", example = "Teclado mecânico com switches azuis")
    String description,

    @Schema(description = "Preço unitário", example = "299.90")
    BigDecimal price,

    @Schema(description = "Saldo em estoque cadastrado", example = "50")
    Integer quantity,

    @Schema(description = "Nome da categoria associada", example = "Periféricos")
    String categoryName
) {}