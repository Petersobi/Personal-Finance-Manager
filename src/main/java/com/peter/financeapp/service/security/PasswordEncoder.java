package com.peter.financeapp.service.security;

public interface PasswordEncoder {
    String encode(String rawPassword);
    Boolean matches(String rawPassword, String hashPassword);
}
