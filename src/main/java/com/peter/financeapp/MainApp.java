package com.peter.financeapp;

import com.peter.financeapp.service.AppConfig;
import com.peter.financeapp.service.AuthException;
import com.peter.financeapp.service.AuthService;
import com.peter.financeapp.util.DButil;


public class MainApp {
     public static void main(String[] args) {
         DButil.initializeDataBase();
         AuthService authService =AppConfig.authService();

    }
}
