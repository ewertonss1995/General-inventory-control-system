package com.inventory.control.web.system.adapters.exception;

public class InvalidCsvHeaderException extends CsvValidationException {
    public InvalidCsvHeaderException(String message) {
        super(message);
    }
}