package com.inventory.control.web.system.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para criação ou atualização de uma categoria")
public record CategoryRequest(
    @Schema(description = "Nome único da categoria", example = "Periféricos")
    @NotBlank(message = "Nome da categoria é obrigatório") 
    String name,

    @Schema(description = "Descrição detalhada da categoria", example = "Dispositivos de entrada e saída para computadores")
    String description
) {}