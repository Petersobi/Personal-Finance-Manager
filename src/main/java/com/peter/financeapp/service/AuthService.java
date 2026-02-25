package com.peter.financeapp.service;

import com.peter.financeapp.dao.DataAccessException;
import com.peter.financeapp.model.User;
import com.peter.financeapp.repository.UserRepository;
import com.peter.financeapp.util.HashUtil;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User register(String username, String password){
        validateUsername(username);
        validatePassword(password);

        if(userRepository.findByUsername(username)!= null){
            throw new AuthException("Username already exists!.");
        }
        User user =new User(username,HashUtil.hashPassword(password));
        try {
        userRepository.save(user);} catch (DataAccessException e) {
            throw new AuthException("Unable to register user. try again later.");
        }
        return user;

    }

    public User login(String username,String password) {

        User user = userRepository.findByUsername(username);
        if (user==null){
            return null;
        }
        if (HashUtil.checkPassword(password,user.getPassword())){
            return user;
        }
        return null;
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
}
