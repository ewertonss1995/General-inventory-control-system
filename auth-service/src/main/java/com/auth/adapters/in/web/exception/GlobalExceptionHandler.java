package com.auth.adapters.in.web.exception;

import com.auth.adapters.in.web.exception.FieldErrorRepresentation;
import com.auth.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. Captura as exceções de regras de negócio (BusinessException)
     * Retorna HTTP 422 Unprocessable Entity ou 400 Bad Request conforme a semântica.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, 
                ex.getMessage()
        );
        
        problemDetail.setTitle("Violação de Regra de Negócio");
        problemDetail.setType(URI.create("https://api.inventory-control.com/errors/business-rule-violation"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

    /**
     * 2. Captura erros de validação (@Valid do Bean Validation)
     * Retorna HTTP 400 Bad Request contendo a lista detalhada de campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, 
                "Um ou mais campos da requisição são inválidos. Corrija-os e tente novamente."
        );

        problemDetail.setTitle("Erro de Validação de Dados");
        problemDetail.setType(URI.create("https://api.inventory-control.com/errors/invalid-fields"));
        problemDetail.setProperty("timestamp", Instant.now());

        // Mapeia cada erro de validação para a nossa representação resumida
        List<FieldErrorRepresentation> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorRepresentation(error.getField(), error.getDefaultMessage()))
                .toList();

        problemDetail.setProperty("invalidFields", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    /**
     * 3. Captura qualquer outra exceção genérica inesperada (Internal Server Error)
     * Retorna HTTP 500 para não expor stacktraces internos sensíveis em produção.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
        // Em produção, registre este erro com um logger (Ex: log.error("Erro não esperado", ex))
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Ocorreu um erro interno inesperado no servidor. Por favor, tente novamente mais tarde."
        );

        problemDetail.setTitle("Erro Interno do Servidor");
        problemDetail.setType(URI.create("https://api.inventory-control.com/errors/internal-server-error"));
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}