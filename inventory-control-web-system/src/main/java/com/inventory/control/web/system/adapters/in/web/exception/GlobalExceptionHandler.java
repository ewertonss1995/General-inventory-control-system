package com.inventory.control.web.system.adapters.in.web.exception;

import com.inventory.control.web.system.adapters.out.exception.IntegrationException;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepta erros das integrações HTTP downstream (como as do auth-service)
     * e repassa o mesmo payload estruturado de erro para o Frontend Web.
     */
    // @ExceptionHandler(IntegrationException.class)
    // public ResponseEntity<ProblemDetail> handleIntegrationException(IntegrationException ex) {
    //     return ResponseEntity.status(ex.getStatus()).body(ex);
    // }
}