package com.inventory.control.system.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload com os dados para criação ou atualização de uma categoria")
public record CategoryRequest(

    @Schema(
        description = "Nome único da categoria de produtos",
        example = "Periféricos",
        requiredMode = Schema.RequiredMode.REQUIRED,
        maxLength = 100
    )
    @NotNull(message = "Nome da categoria é obrigatório")
    @NotBlank(message = "Nome da categoria não pode estar em branco")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    String name,

    @Schema(
        description = "Descrição detalhada sobre os produtos pertencentes a esta categoria",
        example = "Dispositivos de entrada e saída, tais como teclados, mouses e monitores",
        maxLength = 255
    )
    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    String description
) {}