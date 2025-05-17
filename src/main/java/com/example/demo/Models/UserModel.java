package com.example.demo.models;

import com.example.demo.Models.UserRole;
import java.util.Objects;

public class UserModel {
    private String token;
    private UserRole role;

    // Конструкторы
    public UserModel() {
    }

    public UserModel(String token, UserRole role) {
        this.token = token;
        this.role = role;
    }

    // Геттеры и сеттеры
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    // equals() и hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(token, userModel.token) && role == userModel.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, role);
    }

    // toString()
    @Override
    public String toString() {
        return "UserModel{" +
                "token='" + token + '\'' +
                ", role=" + role +
                '}';
    }
}