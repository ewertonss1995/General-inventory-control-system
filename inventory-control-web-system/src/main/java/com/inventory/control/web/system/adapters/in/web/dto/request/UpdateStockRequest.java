package com.inventory.control.web.system.adapters.in.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStockRequest(
        @NotNull(message = "A quantidade é obrigatória") @Min(value = 1, message = "A quantidade deve ser de no mínimo 1 item") Integer quantity,

        @NotBlank(message = "O tipo de movimentação (IN/OUT) é obrigatório") String movementType) {
}
