package com.peter.financeapp.model;

import java.time.LocalDate;

public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private LocalDate createdAt;

    public User (Long id,String username,String password,LocalDate createdAt,String firstName,String lastName){
        this.id = id; this.username = username; this.password = password; this.createdAt = createdAt; this.firstName = firstName;this.lastName = lastName;
    }
    public User(String username,String password,LocalDate createdAt,String firstName,String lastName){
        this.username = username; this.password = password; this.createdAt = createdAt; this.firstName = firstName;this.lastName = lastName;
    }


    public void setId(Long id) {
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

    public Long getId() {
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

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
