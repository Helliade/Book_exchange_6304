package com.example.demo.DTO;
import com.example.demo.Models.Book;

import java.util.HashSet;
import java.util.Set;

public class BookShortDTO {
    private Long id;
    private String language;
    private String condition;

    //Геттеры
    public Long getId() { return id; }
    public String getLanguage() { return language; }
    public String getCondition() { return condition; }

    public BookShortDTO(Book book) {
        this.id = book.getId();
        this.language = book.getLanguage();
        this.condition = book.getCondit();
    }

    public static Set<BookShortDTO> convertBooksToShortDTO(Set<Book> books) {
        Set<BookShortDTO> result = new HashSet<>();
        for (Book book : books) {
            result.add(new BookShortDTO(book));
        }
        return result;
    }

    @Override
    public String toString() {
        return "BooksDTO{" +
                "id=" + id +
                ", language='" + language + '\'' +
                ", condition='" + condition +
                '}';
    }
}
