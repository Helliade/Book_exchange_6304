package com.example.demo.DTO;

import com.example.demo.Models.Book;

import java.util.HashSet;
import java.util.Set;

public class BookDTO {
    private Long id;
    private Short yearOfPubl;
    private String publHouse;
    private String language;
    private String condit;
    private String status;
    private Set<WorkShortDTO> works;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

//    public void setId(Long id) {
//        this.id = id;
//    }

    public Short getYearOfPubl() {
        return yearOfPubl;
    }

//    public void setYearOfPubl(Short yearOfPubl) {
//        this.yearOfPubl = yearOfPubl;
//    }

    public String getPublHouse() {
        return publHouse;
    }

//    public void setPublHouse(String publHouse) {
//        this.publHouse = publHouse;
//    }

    public String getLanguage() {
        return language;
    }

//    public void setLanguage(String language) {
//        this.language = language;
//    }

    public String getCondit() {
        return condit;
    }

//    public void setCondit(String condit) {
//        this.condit = condit;
//    }

    public String getStatus() {
        return status;
    }

//    public void setStatus(String status) {
//        this.status = status;
//    }

    public Set<WorkShortDTO> getWorks() {
        return works;
    }

//    public void setWorks(Set<WorkDTO> works) {
//        this.works = works;
//    }

    public BookDTO(Book book) {
        this.id = book.getId();
        this.yearOfPubl = book.getYearOfPubl();
        this.publHouse = book.getPublHouse();
        this.language = book.getLanguage();
        this.condit = book.getCondit();
        this.status = book.getStatus();
        this.works = WorkShortDTO.convertWorksToShortDTO(book.getWorks());
    }

    public static Set<BookDTO> convertBooksToDTO(Set<Book> books) {
        Set<BookDTO> result = new HashSet<>();
        for (Book book : books) {
            result.add(new BookDTO(book));
        }
        return result;
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", yearOfPubl='" + yearOfPubl + '\'' +
                ", publHouse='" + publHouse + '\'' +
                ", language='" + language + '\'' +
                ", condit='" + condit + '\'' +
                ", status='" + status + '\'' +
                ", works='" + works + '\'' +
                '}';
    }
}
