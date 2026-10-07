package com.inventory.control.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados da categoria do produto")
public record CategoryResponse(
    @Schema(description = "ID único da categoria", example = "60d5ec49f1b2c82b1c8e4567")
    String id,

    @Schema(description = "Nome da categoria", example = "Periféricos")
    String name,

    @Schema(description = "Descrição da categoria", example = "Dispositivos de entrada e saída")
    String description
) {}