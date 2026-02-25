package com.peter.financeapp;

import com.peter.financeapp.dao.SQLiteUserDAO;
import com.peter.financeapp.repository.UserRepository;
import com.peter.financeapp.service.AuthException;
import com.peter.financeapp.service.AuthService;
import com.peter.financeapp.util.DButil;

public class MainApp {
     public static void main(String[] args) {
         DButil.initializeDataBase();
         UserRepository sqlUserDAO = new SQLiteUserDAO();
         AuthService authService = new AuthService(sqlUserDAO);
       try {
           authService.register("Pe","1234");

       } catch (AuthException e) {
           System.out.println(e.getMessage());
       }

       try {
           authService.register("peter","123456");
           System.out.println("Username registered successfully!");
       } catch (AuthException e){
           System.out.println(e.getMessage());
       }
    }
}
