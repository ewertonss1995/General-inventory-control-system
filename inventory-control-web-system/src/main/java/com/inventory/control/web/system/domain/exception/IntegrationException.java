package com.inventory.control.web.system.domain.exception;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

@Getter
public class IntegrationException extends RuntimeException {
    
    private final int status;
    private final ProblemDetail problemDetail;

    public IntegrationException(int status, ProblemDetail problemDetail) {
        super(problemDetail.getDetail() != null ? problemDetail.getDetail() : "Falha na comunicação entre serviços.");
        this.status = status;
        this.problemDetail = problemDetail;
    }
}