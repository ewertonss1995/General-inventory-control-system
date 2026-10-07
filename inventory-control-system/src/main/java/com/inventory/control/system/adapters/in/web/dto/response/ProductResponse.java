package com.inventory.control.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Objeto contendo as informações completas do produto")
public record ProductResponse(
    @Schema(description = "Identificador único do produto no banco", example = "60d5ec49f1b2c82b1c8e4999")
    String id,

    @Schema(description = "SKU do produto", example = "PRD-TECL-001")
    String sku,

    @Schema(description = "Nome do produto", example = "Teclado Mecânico RGB")
    String name,

    @Schema(description = "Descrição técnica do produto", example = "Teclado mecânico ABNT2 com iluminação RGB")
    String description,

    @Schema(description = "Preço do produto", example = "299.90")
    BigDecimal price,

    @Schema(description = "Saldo em estoque", example = "50")
    Integer quantity,

    @Schema(description = "Informações detalhadas da categoria associada")
    CategoryResponse category
) {}