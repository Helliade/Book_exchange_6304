package com.example.demo.DTO;

public class ChangePasswordDTO {

    private String oldPassword;   // Старый пароль
    private String newPassword;   // Новый пароль

    // Геттеры и сеттеры (обязательны для работы Spring)

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
