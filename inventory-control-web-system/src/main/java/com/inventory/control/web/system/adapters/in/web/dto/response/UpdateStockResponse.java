package com.inventory.control.web.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado do processamento da movimentação de estoque")
public record UpdateStockResponse(
    @Schema(description = "SKU do produto alterado", example = "PRD-TECL-001")
    String sku,

    @Schema(description = "Quantidade anterior à movimentação", example = "50")
    Integer previousQuantity,

    @Schema(description = "Nova quantidade em estoque após alteração", example = "60")
    Integer newQuantity, 

    @Schema(description = "Tipo de movimentação executada", example = "IN")
    String movementType, 

    @Schema(description = "Mensagem com status/resumo da operação", example = "Estoque atualizado com sucesso.")
    String message
) {}