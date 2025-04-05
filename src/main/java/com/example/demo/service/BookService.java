package com.example.demo.service;

import com.example.demo.Models.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    //Получение списка всех книг
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    //Получение заказа по ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    //Создание заказа
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    //Обновление заказа
    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    //Удаление заказа
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
