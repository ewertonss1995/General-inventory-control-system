package com.inventory.control.system.adapters.in.web.dto.request;

import com.inventory.control.system.domain.model.enums.StockMovementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados para requisição de movimentação de estoque")
public record UpdateStockRequest(
    @Schema(description = "Quantidade de itens para movimentar", example = "10", minimum = "1")
    @NotNull(message = "A quantidade é obrigatória") 
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1 item") 
    Integer quantity,

    @Schema(description = "Tipo de movimentação: IN (Entrada) ou OUT (Saída)", example = "IN")
    @NotNull(message = "O tipo de movimentação (IN/OUT) é obrigatório") 
    StockMovementType movementType
) {}