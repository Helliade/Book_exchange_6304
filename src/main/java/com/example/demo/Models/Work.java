package com.example.demo.Models;
import jakarta.persistence.*; // Используйте jakarta.persistence, если у вас Spring Boot 3+

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Creation") // Опционально: указать имя таблицы, если отличается от имени класса
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматическая генерация ID базой данных
    private Long id;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String name;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String writer;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String genre;

    @Column(nullable = true)
    private Short year;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="Book_Creation",
            joinColumns=  @JoinColumn(name="Creation_id", referencedColumnName="id"),
            inverseJoinColumns= @JoinColumn(name="Book_id", referencedColumnName="id") )
    private Set<Book> books = new HashSet<Book>();

    // Конструкторы
    public Work() {
        // Пустой конструктор для JPA
    }

    public Work(String name, String writer, String genre, Short year) {
        this.name = name;
        this.writer = writer;
        this.genre = genre;
        this.year = year;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getWriter() {
        return writer;
    }
    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Short getYear() {
        return year;
    }
    public void setYear(Short year) {
        this.year = year;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Work work = (Work) o;
        return Objects.equals(id, work.id); // Пример: сравнение по ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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