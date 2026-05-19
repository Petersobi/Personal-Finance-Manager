package com.peter.financeapp.util;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtils {
    public static void requireNotBlank(String value,String field,ValidationResult result,String message) {
        if (value == null || value.isBlank()) {
            result.addError(field,message);
        }
    }
    public static void requireNotNull(Object value, String field , ValidationResult result,String message){
        if (value == null) {
            result.addError(field,message);
        }
    }
}
