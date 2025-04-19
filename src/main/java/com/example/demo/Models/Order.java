package com.example.demo.Models;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


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

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Username user;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="Booking_Book",
            joinColumns=  @JoinColumn(name="Booking_id", referencedColumnName="id"),
            inverseJoinColumns= @JoinColumn(name="Book_id", referencedColumnName="id") )
    private Set<Book> books = new HashSet<>();

//    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
//    private Long user_id;

    // Конструкторы
    public Order() {
        // Пустой конструктор для JPA
    }

    public Order(String type, String status, Username user) {
        this.type = type;
        this.status = status;
        this.user = user;
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

//    public Username getUser() {
//        return user;
//    }
    public void setUser(Username user) {
        this.user = user;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id); // Пример: сравнение по ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", user=" + user.getLogin() +
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
//@ManyToOne - связь многие-к-одному по конкретному полю.