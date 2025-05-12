package com.example.demo.DTO;

import com.example.demo.Models.Work;

import java.util.HashSet;
import java.util.Set;

public class WorkDTO {
    private Long id;
    private String name;
    private String writer;
    private String genre;
    private Short year;
    private Set<BookShortDTO> books;

    // Геттеры
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWriter() {
        return writer;
    }

    public String getGenre() {
        return genre;
    }

    public Short getYear() {
        return year;
    }

    public Set<BookShortDTO> getBooks() {
        return books;
    }

    public WorkDTO(Work work) {
        this.id = work.getId();
        this.name = work.getName();
        this.writer = work.getWriter();
        this.genre = work.getGenre();
        this.year = work.getYear();
        this.books = BookShortDTO.convertBooksToShortDTO(work.getBooks());
    }

    public static Set<WorkDTO> convertWorksToDTO(Set<Work> works) {
        Set<WorkDTO> result = new HashSet<>();
        for (Work work : works) {
            result.add(new WorkDTO(work));
        }
        return result;
    }

    @Override
    public String toString() {
        return "WorkDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", writer='" + writer + '\'' +
                ", genre='" + genre + '\'' +
                ", year='" + year + '\'' +
                ", books='" + books +
                '}';
    }
}
