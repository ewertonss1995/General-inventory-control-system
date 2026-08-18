package com.inventory.control.web.system.adapters.exception;

import com.inventory.control.web.system.adapters.in.web.dto.response.ErrorResponse;
import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata erros de integração vindo do Feign / CustomFeignErrorDecoder.
     * Repassa exatamente o código HTTP original do serviço downstream.
     */
    @ExceptionHandler(IntegrationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrationException(IntegrationException ex, HttpServletRequest request) {
        log.error("Exceção de Integração capturada | Status: {} | Mensagem: {}", ex.getStatus(), ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                ex.getStatus(),
                HttpStatus.valueOf(ex.getStatus()).getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    /**
     * Trata exceções de regras de negócio (ex: Saldo insuficiente, transação inválida).
     * Retorna HTTP 400 Bad Request.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.error("Regra de negócio violada: {}", ex.getMessage());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Trata cenários em que um recurso solicitado não existe.
     * Retorna HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Recurso não encontrado: {}", ex.getMessage());

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Trata erros de validação de DTOs do Spring Validation (@Valid / @NotNull / @NotBlank).
     * Mapeia os campos com erro diretamente para a lista 'fieldErrors' do ErrorResponse.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(field -> new ErrorResponse.FieldError(field.getField(), field.getDefaultMessage()))
                .toList();

        log.error("Erro de validação nos dados de entrada para a rota {}: {} erro(s)", request.getRequestURI(), fieldErrors.size());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = ErrorResponse.ofValidation(
                status.value(),
                status.getReasonPhrase(),
                "Erro na validação dos campos informados. " + ex.getMessage(),
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request){
        log.error("Erro de validação do corpo da requisição na rota {}: ", request.getRequestURI(), ex);

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "Corpo da requisição inválido: " + ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
        }

    /**
     * Captura qualquer exceção não tratada (ex: NullPointerException, falhas de infraestrutura).
     * Retorna HTTP 500 Internal Server Error protegendo os detalhes sensíveis do backend.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Erro interno não tratado no BFF na rota {}: ", request.getRequestURI(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                "Ocorreu um erro interno no sistema. Por favor, tente novamente mais tarde. " + ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }
}
