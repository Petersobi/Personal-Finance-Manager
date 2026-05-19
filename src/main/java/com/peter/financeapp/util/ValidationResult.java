package com.peter.financeapp.util;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
    private final Map<String,String> errors = new HashMap<>();

    public void addError(String field,String error){
        errors.put(field,error);
    }
    public boolean hasErrors(){
        return !errors.isEmpty();
    }
    public String getError(String field){
        return errors.get(field);
    }
    public Map<String,String> getAllErrors(){
        return errors;
    }
}
