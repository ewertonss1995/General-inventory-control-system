package com.inventory.control.web.system.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Requisição para atualização/movimentação de estoque")
public record UpdateStockRequest(
        @Schema(description = "Quantidade de itens a movimentar", example = "10", minimum = "1")
        @NotNull(message = "A quantidade é obrigatória") 
        @Min(value = 1, message = "A quantidade deve ser de no mínimo 1 item") 
        Integer quantity,

        @Schema(description = "Tipo de movimentação: IN (Entrada) ou OUT (Saída)", example = "IN", allowableValues = {"IN", "OUT"})
        @NotBlank(message = "O tipo de movimentação (IN/OUT) é obrigatório") 
        String movementType
) {}