package com.peter.financeapp.service.security;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptPasswordEncoder implements PasswordEncoder {

    public  String  encode(String plainPassword){
      return   BCrypt.hashpw(plainPassword,BCrypt.gensalt());
    }
    public  Boolean matches(String rawPassword, String hashedPassword){
        return BCrypt.checkpw(rawPassword,hashedPassword);
    }
}
