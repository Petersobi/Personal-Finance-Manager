package com.peter.financeapp.util.validator;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
