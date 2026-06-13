package com.example.tickets_api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class AppUser {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    private String role;

    public AppUser() {}
    public AppUser(String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "username='" + username + '\'' +
                ", id=" + id +
                ", role='" + role + '\'' +
                '}';
    }
}
