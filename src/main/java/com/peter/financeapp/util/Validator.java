package com.peter.financeapp.util;

public interface Validator<T> {
    ValidationResult validate(T data);
}
