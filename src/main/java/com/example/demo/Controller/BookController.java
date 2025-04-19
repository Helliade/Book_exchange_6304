package com.example.demo.Controller;

import com.example.demo.Models.Book;
import com.example.demo.Models.Order;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

//        GET    /api/books                      - Получить все книги
//        GET    /api/books/{id}                 - Получить книгу по ID
//        POST   /api/books                      - Создать новую книгу
//        PUT    /api/books/{id}                 - Обновить книгу
//        PATCH  /api/books/{id}/status          - Изменить статус книги
//        PATCH  /api/books/{id}/condition       - Изменить состояние книги
//        DELETE /api/books/{id}                 - Удалить книгу
//  ?      GET    /api/books?status=FREE          - Получить свободные книги
//  ?      GET    /api/books?condition=PERFECT    - Фильтрация по состоянию
//  !!!!      GET    /api/books/{id}/creations       - Получить произведения книги
//  !!!!      POST   /api/books/{id}/creations       - Связать книгу с произведением

@RestController
@RequestMapping("/api/books")
public class BookController {


    private final BookService bookService;


    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

//GET
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }


    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

//POST
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

//PUT
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        return bookService.updateBook(book);
    }
//PATCH
    @PatchMapping("/{id}/status")
    public ResponseEntity<Book> updateBookStatus(
            @PathVariable Long bookId,
            @RequestParam String status) {

        if (status == null || status.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status parameter cannot be blank"
            );
        }

        Book updatedBook = bookService.updateBookStatus(bookId, status);
        return ResponseEntity.ok(updatedBook);
    }

    @PatchMapping("/{id}/condition")
    public ResponseEntity<Book> updateBookCondit(
            @PathVariable Long bookId,
            @RequestParam String condit) {

        if (condit == null || condit.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status parameter cannot be blank"
            );
        }

        Book updatedBook = bookService.updateBookCondit(bookId, condit);
        return ResponseEntity.ok(updatedBook);
    }

//DELETE
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
