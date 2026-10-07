package com.inventory.control.system.adapters.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Estrutura padrão para retorno de erros da aplicação")
public record ErrorResponse(
    @Schema(description = "Código do status HTTP", example = "400")
    int status,

    @Schema(description = "Título descritivo do tipo de erro", example = "Regra de Negócio Violada")
    String title,

    @Schema(description = "Mensagem detalhada do erro gerado", example = "Saldo insuficiente para o produto SKU 'PRD-TECL-001'.")
    String message,

    @Schema(description = "Timestamp com data e hora UTC do momento do erro", example = "2026-10-07T11:26:00Z")
    Instant timestamp,

    @Schema(description = "Lista de erros de validação de campo (quando aplicável)")
    List<FieldError> errors
) {
    @Schema(description = "Detalhe do campo que apresentou falha de validação")
    public record FieldError(
        @Schema(description = "Nome do campo com erro", example = "price")
        String field, 

        @Schema(description = "Mensagem do erro do campo", example = "Preço é obrigatório")
        String message
    ) {}

    public ErrorResponse(int status, String title, String message) {
        this(status, title, message, Instant.now(), List.of());
    }

    public ErrorResponse(int status, String title, String message, List<FieldError> errors) {
        this(status, title, message, Instant.now(), errors);
    }
}