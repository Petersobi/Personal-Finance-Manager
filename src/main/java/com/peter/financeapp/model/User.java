package com.peter.financeapp.model;

import java.time.LocalDate;

public class User {
    private long id;
    private String username;
    private String password;
    private LocalDate createdAt;

    public User (int id,String username,String password,LocalDate createdAt){
        this.id = id; this.username = username; this.password = password; this.createdAt = createdAt;
    }
    public User(String username,String password){
        this.username = username; this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
