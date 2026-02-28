package com.peter.financeapp.service;

import com.peter.financeapp.dao.SQLiteUserDAO;
import com.peter.financeapp.service.security.PasswordEncoder;
import com.peter.financeapp.repository.UserRepository;
import com.peter.financeapp.service.security.BcryptPasswordEncoder;
import com.peter.financeapp.session.SessionManager;

public class AppConfig {
    public static AuthService authService () {
        UserRepository userRepository = new SQLiteUserDAO();
        PasswordEncoder passwordEncoder = new BcryptPasswordEncoder();
        SessionManager sessionManager = new SessionManager();

        return new AuthService(userRepository,passwordEncoder,sessionManager);
    }
}
