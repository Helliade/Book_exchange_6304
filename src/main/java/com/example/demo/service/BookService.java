package com.example.demo.service;

import com.example.demo.Models.Book;
import com.example.demo.Models.Order;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class BookService {
    private static final Set<String> SVALID_STATUSES = Set.of(
            "FREE", "BOOKED", "OUT"
    );
    private static final Set<String> CVALID_STATUSES = Set.of(
            "PERFECT", "HAS_FLAWS", "DAMAGED"
    );

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

    public Book updateBookStatus(Long bookId, String newStatus) {
        // Валидация статуса
        if (!SVALID_STATUSES.contains(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status. Allowed values: " + SVALID_STATUSES
            );
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found with id: " + bookId
                ));

        //TODO Проверка допустимости перехода статусов
//        validateStatusTransition(book.getStatus(), newStatus);

        book.setStatus(newStatus);
        return bookRepository.save(book);
    }

    public Book updateBookCondit(Long bookId, String newCondit) {
        // Валидация статуса
        if (!CVALID_STATUSES.contains(newCondit)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid condition. Allowed values: " + CVALID_STATUSES
            );
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found with id: " + bookId
                ));

        //TODO Проверка допустимости перехода статусов
//        validateConditTransition(book.getCondit(), newCondit);

        book.setStatus(newCondit);
        return bookRepository.save(book);
    }
}
