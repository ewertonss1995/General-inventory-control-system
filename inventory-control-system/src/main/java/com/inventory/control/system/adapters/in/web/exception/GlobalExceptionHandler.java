package com.inventory.control.system.adapters.in.web.exception;

import com.inventory.control.system.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, 
                ex.getMessage()
        );
        
        problemDetail.setTitle("Violação de Regra de Negócio");
        problemDetail.setType(URI.create("https://api.inventory-control.com/errors"));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }
}
