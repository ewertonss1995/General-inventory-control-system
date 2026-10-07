package com.inventory.control.web.system.adapters.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura padronizada para retorno de falhas e erros da aplicação")
public record ErrorResponse(
    @Schema(description = "Código de status HTTP da resposta", example = "400")
    int status,

    @Schema(description = "Descrição textual do status HTTP", example = "Bad Request")
    String error,

    @Schema(description = "Mensagem com o detalhamento do erro ocorrido", example = "Erro na validação dos campos informados.")
    String message,

    @Schema(description = "Caminho relativo do endpoint chamado", example = "/api/v1/auth/register")
    String path,

    @Schema(description = "Data e hora do registro da ocorrência em UTC", example = "2026-10-07T11:40:00Z")
    Instant timestamp,

    @Schema(description = "Lista detalhada de inconsistências encontradas em campos do corpo da requisição")
    List<FieldError> fieldErrors
) {
    @Schema(description = "Detalhamento de erro por campo específico")
    public record FieldError(
        @Schema(description = "Nome do parâmetro/campo com erro", example = "username")
        String field,

        @Schema(description = "Mensagem descritiva da regra violada pelo campo", example = "Username é obrigatório")
        String message
    ) {}

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now(), null);
    }

    public static ErrorResponse ofValidation(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(status, error, message, path, Instant.now(), fieldErrors);
    }
}