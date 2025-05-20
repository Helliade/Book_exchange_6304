package com.example.demo.Models;

import com.example.demo.Models.UserRole;
import java.util.Objects;

public class UserModel {
    private String accessToken;
    private String refreshToken;
    private UserRole role;

    // Конструкторы
    public UserModel() {
    }

    public UserModel(String accessToken, String refreshToken, UserRole role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    // Геттеры и сеттеры
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
        return Objects.equals(accessToken, userModel.accessToken) && Objects.equals(refreshToken, userModel.refreshToken) && role == userModel.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken, role);
    }

    // toString()
    @Override
    public String toString() {
        return "UserModel{" +
                "accessToken='" + accessToken + '\'' +
                "refreshToken='" + refreshToken + '\'' +
                ", role=" + role +
                '}';
    }
}