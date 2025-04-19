package com.example.demo.Models;
import jakarta.persistence.*; // Используйте jakarta.persistence, если у вас Spring Boot 3+

@Entity
@Table // Опционально: указать имя таблицы, если отличается от имени класса
public class Username {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматическая генерация ID базой данных
    private Long id;

    @Column(unique = true, nullable = false) // Указывает, что поле должно быть уникальным и не может быть null в базе данных
    private String login;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String password;

    // Конструкторы
    public Username() {
        // Пустой конструктор для JPA
    }

    public Username(String login, String password) {
        this.login = login;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "Username{" +
                "id=" + id +
                ", login='" + login + '\'' +
                '}';
    }
}

//Аннотации JPA:
//
//@Entity: Указывает, что класс является сущностью JPA и должен быть сопоставлен с таблицей в базе данных.
//@Table(name = "users"): Опционально, задает имя таблицы в базе данных. Если не указано, используется имя класса (в данном случае User -> таблица user).
//@Id: Указывает, что поле является первичным ключом таблицы.
//@GeneratedValue(strategy = GenerationType.IDENTITY): Определяет стратегию генерации первичного ключа. GenerationType.IDENTITY обычно используется для автоинкрементных столбцов в большинстве баз данных.
//@Column(name = "login", nullable = false): Настраивает столбец в базе данных, соответствующий полю username. nullable = false указывает, что столбец не может быть NULL.
//@Column(unique = true): Указывает, что столбец должен быть уникальным.