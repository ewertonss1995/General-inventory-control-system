package com.inventory.control.web.system.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Dados para criação/atualização de um produto")
public record ProductRequest(
        @Schema(description = "Código SKU único do produto", example = "PRD-TECL-001")
        @NotBlank(message = "SKU é obrigatório") 
        String sku,

        @Schema(description = "Nome comercial do produto", example = "Teclado Mecânico RGB")
        @NotBlank(message = "Nome é obrigatório") 
        String name,

        @Schema(description = "Descrição detalhada do produto", example = "Teclado mecânico com switches azuis e retroiluminação RGB")
        String description,

        @Schema(description = "Preço unitário de venda do produto", example = "299.90", minimum = "0.0")
        @NotNull(message = "Preço é obrigatório") 
        @DecimalMin("0.0") 
        BigDecimal price,

        @Schema(description = "Quantidade inicial em estoque", example = "50", minimum = "0")
        @NotNull(message = "Quantidade é obrigatória") 
        @Min(0) 
        Integer quantity,

        @Schema(description = "Identificador único da categoria associada", example = "60d5ec49f1b2c82b1c8e4567")
        @NotNull(message = "Id da categoria é obrigatório") 
        String categoryId
) {}