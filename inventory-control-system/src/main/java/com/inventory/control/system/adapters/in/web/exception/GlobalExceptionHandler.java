package com.inventory.control.system.adapters.in.web.exception;

import com.inventory.control.system.adapters.in.web.dto.ErrorResponse;
import com.inventory.control.system.domain.exception.BusinessException;
import com.inventory.control.system.domain.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepta erros puristas de regra de negócio do Domínio (BusinessException)
     * Retorna HTTP 400 (Bad Request) ou 422 (Unprocessable Entity) com o motivo limpo.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Regra de Negócio Violada",
            ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Intercepta falhas no @Valid dos DTOs (ex: @NotBlank, @Min)
     * Retorna HTTP 400 mapeando exatamente quais campos falharam.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(field -> new ErrorResponse.FieldError(field.getField(), field.getDefaultMessage()))
            .toList();

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de Validação de Payload",
            "Um ou mais campos do formulário/requisição estão inválidos.",
            fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro Interno no Servidor",
            "Ocorreu um erro inesperado ao processar a requisição. Tente novamente mais tarde."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Recurso Não Encontrado",
            ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}