package com.example.demo.service;


import com.example.demo.DTO.WorkDTO;
import com.example.demo.Models.Book;
import com.example.demo.Models.Work;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.WorkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkService {

    private final WorkRepository workRepository;
    private final BookRepository bookRepository;

    @Autowired
    public WorkService(WorkRepository workRepository, BookRepository bookRepository) {
        this.workRepository = workRepository;
        this.bookRepository = bookRepository;
    }

    public static Specification<Work> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.equal(root.get("name"), name);
    }

    public static Specification<Work> hasWriter(String writer) {
        return (root, query, cb) ->
                writer == null ? null : cb.equal(root.get("writer"), writer);
    }

    public static Specification<Work> hasGenre(String genre) {
        return (root, query, cb) ->
                genre == null ? null : cb.equal(root.get("genre"), genre);
    }

    public static Specification<Work> hasYear(Short year) {
        return (root, query, cb) ->
                year == null ? null : cb.equal(root.get("year"), year);
    }




    //Получение списка всех заказов
    public List<Work> getAllWorks() {
        List<Work> works = workRepository.findAll();
        if (works.isEmpty()) {
            throw new EntityNotFoundException("No works found.");
        }
        return works;
    }

    //Получение заказа по ID
    public Work getWorkById(Long workId) {
        return workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + workId));
    }

    //Создание заказа
//    public Work createWork(Work work) {
//        return workRepository.save(work);
//    }
    //Обновление заказа
//    public Work updateWork(Work work) {
//        return workRepository.save(work);
//    }

    //Удаление заказа
    public void deleteWork(Long workId) {
        workRepository.findById(workId)                           //находим заказ
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));
        bookRepository.deleteById(workId);
    }

    //Удаление связанных с произведением данных
    public void deleteWorkLinkedData(Long workId) {
        Work work = workRepository.findById(workId)                           //находим заказ
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));

        // Удаляем связи из книг
        for (Book book : work.getBooks()) {
            book.getWorks().remove(work);
            bookRepository.save(book);
        }
    }

    public List<Work> getWorksByArguments(String name, String writer, String genre, Short year) {

        // Проверка, что хотя бы один параметр задан
        if (name == null && writer == null && genre == null
                && year == null) {
            throw new IllegalArgumentException("At least one parameter must be provided");
        }

        // Динамическое построение запроса
        Specification<Work> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and(hasName(name));
        }
        if (writer != null) {
            spec = spec.and(hasWriter(writer));
        }
        if (genre != null) {
            spec = spec.and(hasGenre(genre));
        }
        if (year != null) {
            spec = spec.and(hasYear(year));
        }

        List<Work> works = workRepository.findAll(spec);

        // Проверка результатов
        if (works.isEmpty()) {
            StringBuilder message = new StringBuilder("No books found.");
            if (name != null) message.append(" Searched for name: ").append(name).append(".");
            if (writer != null) message.append(" Searched for writer: ").append(writer).append(".");
            if (genre != null) message.append(" Searched for genre: ").append(genre).append(".");
            if (year != null) message.append(" Searched for year: ").append(year).append(".");
            throw new EntityNotFoundException(message.toString());
        }
        return works;
    }

    public List<Book> getBooksByWorkId(Long workId) {
        workRepository.findById(workId)
                .orElseThrow(() -> new EntityNotFoundException("Work not found with id: " + workId));
        List<Book> books = bookRepository.findByWorkId(workId);
        if (books.isEmpty()) {
            throw new EntityNotFoundException("No books found.");
        }
        return books;
    }

    public Work createWork(String name, String writer, String genre, Short year) {
        Work work = new Work(name, writer, genre, year);
        return workRepository.save(work);
    }
}