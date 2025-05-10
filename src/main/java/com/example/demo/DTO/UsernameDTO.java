package com.example.demo.DTO;

import com.example.demo.Models.Username;

public class UsernameDTO {
    private Long id;
    private String login;

    // Конструкторы
    public UsernameDTO() {}

    public UsernameDTO(Username username) {
        this.id = username.getId();
        this.login = username.getLogin();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "UsernameDTO{" +
                "id=" + id +
                ", login='" + login +
                '}';
    }
}
