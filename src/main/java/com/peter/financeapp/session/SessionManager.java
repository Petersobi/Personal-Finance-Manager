package com.peter.financeapp.session;

import com.peter.financeapp.model.User;

public class SessionManager {
    private User currentUser;

    public void login(User currentUser){
        this.currentUser = currentUser;
    }
    public void logout(){
        this.currentUser = null;
    }

    public User getCurrentUser() {
        if (currentUser == null){
            throw new IllegalArgumentException("No user is currently logged in.");
        }
        return currentUser;
    }
    public Boolean isloggedIn(){
        return currentUser!=null;
    }
}
