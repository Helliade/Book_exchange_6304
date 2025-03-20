package com.example.demo.Models;
import jakarta.persistence.*; // Используйте jakarta.persistence, если у вас Spring Boot 3+

@Entity
@Table // Опционально: указать имя таблицы, если отличается от имени класса
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматическая генерация ID базой данных
    private Long id;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private Short year_of_publ;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String publ_house;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String language;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String condit;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String status;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Short getYear_of_publ() {
        return year_of_publ;
    }
    public void setYear_of_publ(Short year_of_publ) {
        this.year_of_publ = year_of_publ;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPubl_house() {
        return publ_house;
    }
    public void setPubl_house(String publ_house) {
        this.publ_house = publ_house;
    }

    public String getCondit() {
        return condit;
    }
    public void setCondit(String condit) {
        this.condit = condit;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
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