package com.peter.financeapp.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {
    public static String  hashPassword(String password){
      return   BCrypt.hashpw(password,BCrypt.gensalt());
    }
    public static Boolean checkPassword(String plainPassword,String hashedPassword){
        return BCrypt.checkpw(plainPassword,hashedPassword);
    }
}
