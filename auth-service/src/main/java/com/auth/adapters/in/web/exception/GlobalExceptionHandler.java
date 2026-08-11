package com.auth.adapters.in.web.exception;

import com.auth.adapters.in.web.dto.ErrorResponse;
import com.auth.domain.exception.BusinessException;
import com.auth.adapters.out.exception.DatabaseException;
import com.auth.adapters.out.exception.PasswordEncryptionException;
import com.auth.adapters.out.exception.TokenGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata erros de Regra de Negócio do Domínio (Ex: Usuário já cadastrado, Email duplicado).
     * Mapeia para HTTP 400 Bad Request ou HTTP 422 Unprocessable Entity.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Violação de regra de negócio: {} | URI: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Regra de Negócio Violada",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata falhas de validação dos DTOs do Controller (@Valid / @NotBlank / etc).
     * Retorna HTTP 400 Bad Request detalhando os campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Erro de validação na requisição | URI: {}", request.getRequestURI());

        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(field -> new ErrorResponse.FieldError(field.getField(), field.getDefaultMessage()))
                .toList();

        ErrorResponse error = ErrorResponse.ofValidation(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação nos Dados Enviados",
                "Um ou mais campos contêm valores inválidos. Verifique os detalhes.",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata violações de integridade no banco de dados (Unique Keys, Foreign Keys, Not Null).
     * Retorna HTTP 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Conflito de integridade de dados no banco | URI: {} | Causa: {}", request.getRequestURI(), ex.getMostSpecificCause().getMessage());

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.CONFLICT.value(),
                "Conflito de Dados",
                "Já existe um registro com os dados informados ou o formato é incompatível com as regras de integridade.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Trata falhas de Persistência/Banco de Dados capturadas pelos seus Adapters.
     * Retorna HTTP 500 sem expor detalhes sensíveis da consulta SQL para o cliente.
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException ex, HttpServletRequest request) {
        log.error("Erro interno no banco de dados | URI: {}", request.getRequestURI(), ex);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro de Persistência",
                "Ocorreu uma falha ao comunicar com o armazenamento de dados. Tente novamente mais tarde.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Trata falhas na Criptografia de Senha ou Geração de Tokens JWT (Adapters de Infraestrutura).
     */
    @ExceptionHandler({TokenGenerationException.class, PasswordEncryptionException.class})
    public ResponseEntity<ErrorResponse> handleSecurityInfrastructureException(RuntimeException ex, HttpServletRequest request) {
        log.error("Erro no serviço de segurança/criptografia | URI: {}", request.getRequestURI(), ex);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno de Segurança",
                "Não foi possível processar as credenciais ou gerar o token de acesso.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Captura qualquer outra exceção não mapeada (Exceções genéricas / NullPointerException).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Erro não tratado detectado pela aplicação | URI: {}", request.getRequestURI(), ex);

        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado no sistema. Entre em contato com o suporte.",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
