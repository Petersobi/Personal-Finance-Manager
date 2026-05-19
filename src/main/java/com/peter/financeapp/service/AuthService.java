package com.peter.financeapp.service;

import com.peter.financeapp.dao.DataAccessException;
import com.peter.financeapp.model.User;
import com.peter.financeapp.service.security.PasswordEncoder;
import com.peter.financeapp.repository.UserRepository;
import com.peter.financeapp.session.SessionManager;

import java.time.LocalDate;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionManager sessionManager;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,SessionManager sessionManager){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionManager = sessionManager;

    }

    public User register(String username, String password,String firstName,String lastName){
        validateUsername(username);
        validatePassword(password);
        validateName(firstName);
        validateName(lastName);

        if(userRepository.findByUsername(username)!= null){
            throw new AuthException("Username already exists!.");
        }
        String encodedPassword = passwordEncoder.encode(password);
        LocalDate createdAt = LocalDate.now();
        User user =new User(username,encodedPassword,createdAt,firstName,lastName);
        try {
        userRepository.save(user);} catch (DataAccessException e) {
            throw new AuthException("Unable to register user. try again later.");
        }
        sessionManager.login(user);
        return user;

    }

    public User login(String username,String password) {

        if(username.isEmpty()&&password.isEmpty()){
            throw new AuthException("enter your credentials");
        }
        if(username.isEmpty()){
            throw new AuthException("enter your username");
        }
        if(password.isEmpty()){
            throw new AuthException("enter your password");
        }
        User user = userRepository.findByUsername(username);
        if (user==null){
            throw new AuthException("invalid credentials");
        }
        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new AuthException("invalid credentials");
        }
        sessionManager.login(user);
        return user;
    }
    public void logout(){
        sessionManager.logout();
    }

    public void validateUsername(String username){
        if(username==null||username.length()<3){
            throw new AuthException("username must be at least 3 characters long");
        }
    }
    public void validatePassword(String password){
        if (password==null||password.length()<6){
            throw new AuthException("password must be at least 6 characters long");
        }
    }
    public void validateName(String name){
        if(name==null){
            throw new AuthException("name cannot be null");
        }
    }
}
