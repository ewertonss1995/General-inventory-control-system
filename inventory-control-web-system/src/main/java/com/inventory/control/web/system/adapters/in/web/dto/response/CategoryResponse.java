package com.inventory.control.web.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação detalhada de uma categoria cadastrada")
public record CategoryResponse(
    @Schema(description = "Identificador único da categoria no banco de dados", example = "60d5ec49f1b2c82b1c8e4567")
    String id,

    @Schema(description = "Nome da categoria", example = "Periféricos")
    String name,

    @Schema(description = "Descrição detalhada da categoria", example = "Dispositivos de entrada e saída para computadores")
    String description
) {}