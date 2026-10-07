package com.inventory.control.web.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Detalhamento completo do produto cadastrado")
public record ProductResponse(
    @Schema(description = "Identificador único no banco de dados", example = "60d5ec49f1b2c82b1c8e4999")
    String id,

    @Schema(description = "SKU do produto", example = "PRD-TECL-001")
    String sku,

    @Schema(description = "Nome do produto", example = "Teclado Mecânico RGB")
    String name,

    @Schema(description = "Descrição detalhada", example = "Teclado mecânico com switches azuis e retroiluminação RGB")
    String description,

    @Schema(description = "Preço unitário", example = "299.90")
    BigDecimal price,

    @Schema(description = "Saldo atual em estoque", example = "50")
    Integer quantity,

    @Schema(description = "Objeto contendo os dados da categoria associada")
    CategoryResponse category
) {}