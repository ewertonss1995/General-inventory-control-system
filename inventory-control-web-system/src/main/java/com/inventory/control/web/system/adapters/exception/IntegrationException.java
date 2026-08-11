package com.inventory.control.web.system.adapters.exception;

import com.inventory.control.web.system.adapters.in.web.dto.response.ErrorResponse;

public class IntegrationException extends RuntimeException {

    private final int status;
    private final ErrorResponse errorResponse;

    public IntegrationException(int status, ErrorResponse errorResponse) {
        super(errorResponse.message());
        this.status = status;
        this.errorResponse = errorResponse;
    }

    public IntegrationException(int status, String message) {
        super(message);
        this.status = status;
        this.errorResponse = null;
    }

    public int getStatus() {
        return status;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
