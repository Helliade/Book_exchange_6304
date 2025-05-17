package com.example.demo.config;

import org.springframework.stereotype.Component;

import com.example.demo.Models.Username;
import com.example.demo.Models.UserRole;

@Component
public class MySecurity {

    public boolean isAdmin(Username username) {
        return username.getRole() == UserRole.ADMIN;
    }

    public boolean isUser(Username username) {
        return username.getRole() == UserRole.USER;
    }

}



