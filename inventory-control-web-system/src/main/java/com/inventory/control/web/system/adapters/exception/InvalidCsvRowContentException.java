package com.inventory.control.web.system.adapters.exception;

public class InvalidCsvRowContentException extends CsvValidationException {
    public InvalidCsvRowContentException(long lineNumber, String message) {
        super(String.format("Erro na linha %d: %s", lineNumber, message));
    }
}