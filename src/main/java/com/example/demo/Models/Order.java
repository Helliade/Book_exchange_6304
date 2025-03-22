package com.example.demo.Models;

import jakarta.persistence.*; // Используйте jakarta.persistence, если у вас Spring Boot 3+

@Entity
@Table(name = "Booking") // Опционально: указать имя таблицы, если отличается от имени класса
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматическая генерация ID базой данных
    private Long id;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String type;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String status;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private Long user_id;

    // Конструкторы
    public Order() {
        // Пустой конструктор для JPA
    }

    public Order(String type, String status, Long user_id) {
        this.type = type;
        this.status = status;
        this.user_id = user_id;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUser_id() {
        return user_id;
    }
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
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