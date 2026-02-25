package com.peter.financeapp.repository;

import com.peter.financeapp.model.User;

public interface UserRepository {
    void save(User user);
    User findByUsername(String username);
}
