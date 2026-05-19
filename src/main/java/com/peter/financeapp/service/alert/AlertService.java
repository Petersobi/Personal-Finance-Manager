package com.peter.financeapp.service.alert;

public class AlertService {
    public void warnOverspending(String categoryName,String month,String message){
        System.out.println("Budget Alert [" + month + " ]" + categoryName + " -> " + message);
    }
}
