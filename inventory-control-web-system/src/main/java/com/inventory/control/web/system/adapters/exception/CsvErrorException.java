package com.inventory.control.web.system.adapters.exception;

public class CsvErrorException extends RuntimeException {
    
    public CsvErrorException(String message) {
        super(message);
    }

    public CsvErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
