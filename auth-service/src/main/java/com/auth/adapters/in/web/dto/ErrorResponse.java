package com.auth.adapters.in.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Objeto padrão de resposta para erros da API")
public record ErrorResponse(
    @Schema(description = "Código de status HTTP do erro", example = "400")
    int status,

    @Schema(description = "Título descritivo da categoria do erro", example = "Erro de Validação nos Dados Enviados")
    String error,

    @Schema(description = "Detalhes resumidos sobre o erro ocorrido", example = "Um ou mais campos contêm valores inválidos. Verifique os detalhes.")
    String message,

    @Schema(description = "Caminho da requisição (URI) que originou o erro", example = "/v1/auth/register")
    String path,

    @Schema(description = "Data e hora em UTC em que o erro foi gerado", example = "2026-10-03T19:19:08Z")
    Instant timestamp,

    @Schema(description = "Lista detalhada dos erros ocorridos por campo (quando aplicável)")
    List<FieldError> fieldErrors
) {
    @Schema(description = "Detalhe do erro específico de um campo")
    public record FieldError(
        @Schema(description = "Nome do campo que falhou na validação", example = "email")
        String field,

        @Schema(description = "Mensagem explicativa do erro de validação do campo", example = "Formato de e-mail inválido")
        String message
    ) {}

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now(), null);
    }

    public static ErrorResponse ofValidation(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(status, error, message, path, Instant.now(), fieldErrors);
    }
}