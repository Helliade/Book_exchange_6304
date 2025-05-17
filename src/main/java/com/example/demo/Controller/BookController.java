package com.example.demo.Controller;

import com.example.demo.DTO.BookDTO;
import com.example.demo.DTO.WorkShortDTO;
import com.example.demo.Models.Book;
import com.example.demo.Models.Work;
import com.example.demo.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;

//        GET    /api/books                          - Получить все книги
//        GET    /api/books/{bookId}                 - Получить книгу по ID
//        GET    /api/books/search                   - Фильтрация
//        GET    /api/books/{bookId}/creations       - Получить произведения книги
//        POST   /api/books                          - Создать новую книгу
//        POST   /api/books/{bookId}/creations       - Связать книгу с произведением
//  #-----      PUT    /api/books/{bookId}                 - Обновить книгу
//        PATCH  /api/books/{bookId}/change          - Изменить статус или состояние книги
//        DELETE /api/books/{bookId}                 - Удалить книгу
//        DELETE /api/books/{bookId}/creations       - Удалить произведение из книги

@RestController
@RequestMapping("/api/books")                                               //это аннотация Spring, которая связывает HTTP-запрос
                                                                            // (URL + метод) с конкретным методом Java-класса
                                                                            //(контроллера).Метка в коде/инструкция
public class BookController {

    private final BookService bookService;


    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

//GET
    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        try {
            List<BookDTO> result = new LinkedList<>();
            for (Book book : bookService.getAllBooks()) {
                result.add(new BookDTO(book));
            }
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBookById(@PathVariable Long bookId) {
        try {
            return ResponseEntity.ok(new BookDTO(bookService.getBookById(bookId)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getBooksByArguments(
            @RequestParam(required = false) Short yearOfPubl,
            @RequestParam(required = false) String publHouse,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String condit,
            @RequestParam(required = false) String status) {

        try {
            List<BookDTO> result = new LinkedList<>();
            for (Book book : bookService.getBooksByArguments(yearOfPubl, publHouse, language, condit, status)) {
                result.add(new BookDTO(book));
            }
            return ResponseEntity.ok(result);                        //Возвращаем DTO с HTTP 200

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{bookId}/creations")
    public ResponseEntity<?> getWorksByBookId(@PathVariable Long bookId) {

        try {
            List<WorkShortDTO> result = new LinkedList<>();
            for (Work work : bookService.getWorksByBookId(bookId)) {
                result.add(new WorkShortDTO(work));
            }
            return ResponseEntity.ok(result);                        //Возвращаем DTO с HTTP 200

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//POST
    @PostMapping
    public ResponseEntity<?> createBook(Short yearOfPubl, String publHouse, String language, String condit) {

        try {
            Book book = bookService.createBook(yearOfPubl, publHouse, language, condit);
            return ResponseEntity.ok(new BookDTO(book));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/{bookId}/creations")
    public ResponseEntity<?> addWorkToBook(
            @PathVariable Long bookId,
            @RequestParam Long workId) {

        try {
            Book updatedBook = bookService.addWorkToBook(bookId, workId);
            return ResponseEntity.ok(new BookDTO(updatedBook));                        //Возвращаем DTO с HTTP 200

        } catch (IllegalStateException e) {
            // Обработка ошибки (произведение уже в книге)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            // Обработка ошибки (произведение или книга не найдены)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//PUT
//    @PutMapping("/{id}")
//    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
//        book.setId(id);
//        return bookService.updateBook(book);
//    }
//PATCH
    @PatchMapping("/{id}/change")
    public ResponseEntity<?> updateBookArguments(
            @PathVariable Long bookId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String condit) {

        try {
            return ResponseEntity.ok(new BookDTO(bookService.updateBookArguments(bookId, status, condit)));                        //Возвращаем DTO с HTTP 200

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//DELETE

    @DeleteMapping("/{bookId}")
    @PreAuthorize("@mySecurity.isAdmin(authentication.principal.user)")
    public ResponseEntity<?> deleteBook(@PathVariable Long bookId) {

        try {
            bookService.deleteBookLinkedData(bookId);
            bookService.deleteBook(bookId);
            return ResponseEntity.ok("Successful");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/{bookId}/creations")
    public ResponseEntity<?> deleteWorkFromBook(
            @PathVariable Long bookId,
            @RequestParam Long workId) {

        try {
            Book updatedBook = bookService.deleteWorkFromBook(bookId, workId);
            return ResponseEntity.ok(new BookDTO(updatedBook));                        //Возвращаем DTO с HTTP 200

        } catch (IllegalStateException e) {
            // Обработка ошибки (произведения нет в книге)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            // Обработка ошибки (произведение или книга не найдены)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
