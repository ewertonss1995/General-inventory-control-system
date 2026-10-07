package com.inventory.control.system.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Dados para requisição de criação ou atualização de produto")
public record ProductRequest(
    @Schema(description = "Código SKU único do produto", example = "PRD-TECL-001")
    @NotBlank(message = "Sku é obrigatório") 
    String sku,

    @Schema(description = "Nome do produto", example = "Teclado Mecânico RGB")
    @NotBlank(message = "Nome é obrigatório") 
    String name,

    @Schema(description = "Descrição com detalhes técnicos do produto", example = "Teclado mecânico ABNT2 com iluminação RGB")
    String description,

    @Schema(description = "Preço unitário do produto", example = "299.90", minimum = "0.0")
    @NotNull(message = "Preço é obrigatório") 
    @DecimalMin("0.0") 
    BigDecimal price,

    @Schema(description = "Quantidade inicial em estoque", example = "50", minimum = "0")
    @NotNull(message = "Quantidade é obrigatória") 
    @Min(0) 
    Integer quantity,

    @Schema(description = "ID da categoria associada no banco de dados", example = "60d5ec49f1b2c82b1c8e4567")
    @NotNull(message = "Id da categoria é obrigatório") 
    String categoryId
) {}