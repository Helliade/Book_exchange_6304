package com.example.demo.Models;
import jakarta.persistence.*; // Используйте jakarta.persistence, если у вас Spring Boot 3+


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table // Опционально: указать имя таблицы, если отличается от имени класса
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автоматическая генерация ID базой данных
    private Long id;

    @Column(name = "year_of_publ", nullable = false)
    private Short yearOfPubl;

    @Column(name = "publ_house",nullable = false) // Указывает, что поле не может быть null в базе данных
    private String publHouse;

    @Column(name = "lang", nullable = false) // Указывает, что поле не может быть null в базе данных
    private String language;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String condit;

    @Column(nullable = false) // Указывает, что поле не может быть null в базе данных
    private String status;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="Booking_Book",
            joinColumns=  @JoinColumn(name="Book_id", referencedColumnName="id"),
            inverseJoinColumns= @JoinColumn(name="Booking_id", referencedColumnName="id") )
    private Set<Order> orders = new HashSet<Order>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="Book_Creation",
            joinColumns=  @JoinColumn(name="Book_id", referencedColumnName="id"),
            inverseJoinColumns= @JoinColumn(name="Creation_id", referencedColumnName="id") )
    private Set<Work> works = new HashSet<Work>();

    // Конструкторы
    public Book() {
        // Пустой конструктор для JPA
    }

    public Book(Short yearOfPubl, String publHouse, String language, String condit, String status) {
        this.yearOfPubl = yearOfPubl;
        this.publHouse = publHouse;
        this.language = language;
        this.condit = condit;
        this.status = status;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Short getYearOfPubl() {
        return yearOfPubl;
    }
    public void setYearOfPubl(Short yearOfPubl) {
        this.yearOfPubl = yearOfPubl;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPublHouse() {
        return publHouse;
    }
    public void setPublHouse(String publHouse) {
        this.publHouse = publHouse;
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

    public Set<Order> getOrders() {
        return orders;
    }

    // Дополнительные методы для удобной работы с коллекцией
    public void addOrder(Order order) {
        this.orders.add(order);
        order.getBooks().add(this); // Обновляем обратную связь
    }

    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.getBooks().remove(this); // Обновляем обратную связь
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id); // Пример: сравнение по ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", yearOfPubl=" + yearOfPubl +
                ", publHouse='" + publHouse + '\'' +
                ", language='" + language + '\'' +
                ", condit='" + condit + '\'' +
                ", status='" + status + '\'' +
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