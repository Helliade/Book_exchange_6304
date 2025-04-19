package com.example.demo.DTO;

public class UsernameDTO {
    private Long id;
    private String login;

    // Конструкторы
    public UsernameDTO() {}

    public UsernameDTO(Long id, String login) {
        this.id = id;
        this.login = login;
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
}
