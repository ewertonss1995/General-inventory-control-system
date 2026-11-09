package com.inventory.control.web.system.adapters.exception;

public class InvalidFileTypeException extends CsvValidationException {
    public InvalidFileTypeException(String message) {
        super(message);
    }
}