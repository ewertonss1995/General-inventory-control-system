package com.inventory.control.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta do processamento da movimentação de estoque")
public record UpdateStockResponse(
    @Schema(description = "SKU do produto alterado", example = "PRD-TECL-001")
    String sku,

    @Schema(description = "Saldo anterior do estoque", example = "50")
    Integer previousQuantity, 

    @Schema(description = "Novo saldo do estoque após movimentação", example = "60")
    Integer newQuantity, 

    @Schema(description = "Tipo de movimentação realizada (IN/OUT)", example = "IN")
    String movementType, 

    @Schema(description = "Mensagem com resumo da alteração realizada", example = "Estoque atualizado com sucesso.")
    String message
) {}