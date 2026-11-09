package com.inventory.control.web.system.adapters.exception;

import com.inventory.control.web.system.adapters.in.web.dto.response.ErrorResponse;
import com.inventory.control.web.system.adapters.exception.CsvValidationException;
import com.inventory.control.web.system.adapters.exception.IntegrationException;
import com.inventory.control.web.system.adapters.exception.InvalidCsvHeaderException;
import com.inventory.control.web.system.adapters.exception.InvalidCsvRowContentException;
import com.inventory.control.web.system.adapters.exception.InvalidFileTypeException;
import com.inventory.control.web.system.domain.exception.BusinessException;
import com.inventory.control.web.system.domain.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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
        log.error("Exceção de Integração capturada | Status: {} | Mensagem: {}", ex.getStatus(), ex.getMessage(), ex);

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
        log.error("Regra de negócio violada: {}", ex.getMessage(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("A regra de negócio foi violada: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /**
     * Trata cenários em que um recurso solicitado não existe.
     * Retorna HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Recurso não encontrado: {}", ex.getMessage(), ex);
        HttpStatus status = HttpStatus.NOT_FOUND;
        String message = String.format("O recurso solicitado não existe: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /**
     * Trata erros de validação de DTOs do Spring Validation (@Valid / @NotNull / @NotBlank).
     * Mapeia os campos com erro diretamente para a lista 'fieldErrors' do ErrorResponse.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(field -> new ErrorResponse.FieldError(field.getField(), field.getDefaultMessage()))
                .toList();

        log.error("Erro de validação nos dados de entrada para a rota {}: {} erro(s)", request.getRequestURI(), fieldErrors.size(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("Erro na validação dos campos informados: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request){
        log.error("Erro de validação do corpo da requisição na rota {}: ", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("Corpo da requisição inválido: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /**
     * Captura qualquer exceção não tratada (ex: NullPointerException, falhas de infraestrutura).
     * Retorna HTTP 500 Internal Server Error protegendo os detalhes sensíveis do backend.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Erro interno não tratado no BFF na rota {}: ", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = String.format("Ocorreu um erro interno no sistema. Por favor, tente novamente mais tarde. %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /** 
     * Tratando quando o arquivo excede o tamanho máximo permitido no application.yml 
     * Retorna HTTP 413 Payload Too Large. 
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("Erro de tamanho de arquivo excedido na rota {}: ", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;
        String message = String.format("O arquivo enviado é muito grande. Verifique o limite permitido: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /** 
     * Tratando quando a parte do arquivo ("file") não foi enviada na requisição 
     * Retorna HTTP 400 Bad Request. 
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex, HttpServletRequest request) {
        log.error("Erro de validação do arquivo na rota {}: ", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("O arquivo obrigatório não foi enviado na requisição: %s", ex.getRequestPartName());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /**
     * Tratando quando o tipo de arquivo enviado não é permitido (ex: .exe, .bat, etc.)
     * Retorna HTTP 400 Bad Request.
     */
    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileTypeException(InvalidFileTypeException ex, HttpServletRequest request) {
        log.error("Erro de validação do tipo de arquivo na rota {}: ", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("O tipo de arquivo enviado é inválido. Verifique o formato permitido: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /**
     * Tratando quando o cabeçalho do arquivo CSV enviado não é válido.
     * Retorna HTTP 400 Bad Request.
     */
    @ExceptionHandler(InvalidCsvHeaderException.class)
    private ResponseEntity<ErrorResponse> handleInvalidCsvHeaderException(InvalidCsvHeaderException ex, HttpServletRequest request) {
        log.error("Erro de validação do cabeçalho do CSV na rota {}: ", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("O cabeçalho do arquivo CSV é inválido. Verifique o formato permitido: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /**
     * Tratando quando o conteúdo de uma linha do arquivo CSV enviado não é válido.
     * Retorna HTTP 400 Bad Request.
     */
    @ExceptionHandler(InvalidCsvRowContentException.class)
    private ResponseEntity<ErrorResponse> handleInvalidCsvRowContentException(InvalidCsvRowContentException ex, HttpServletRequest request) {
        log.error("Erro de validação do conteúdo da linha do CSV na rota {}: ", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("O conteúdo da linha do arquivo CSV é inválido. Verifique o formato permitido: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    /**
     * Tratando quando ocorre algum erro de validação do CSV (ex: cabeçalho inválido, linha inválida, etc.)
     * Retorna HTTP 400 Bad Request.
     */
    @ExceptionHandler(CsvValidationException.class)
    private ResponseEntity<ErrorResponse> handleCsvValidationException(CsvValidationException ex, HttpServletRequest request) {
        log.error("Erro de validação do CSV na rota {}: ", request.getRequestURI(), ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = String.format("O arquivo CSV enviado é inválido. Verifique o formato permitido: %s", ex.getMessage());
        return ResponseEntity.status(status).body(buildErrorResponse(status, message, request.getRequestURI()));
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
        return ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }
}
