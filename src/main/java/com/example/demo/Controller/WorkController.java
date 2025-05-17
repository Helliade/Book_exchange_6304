package com.example.demo.Controller;

import com.example.demo.DTO.*;
import com.example.demo.Models.Book;
import com.example.demo.Models.Order;
import com.example.demo.Models.Work;
import com.example.demo.service.WorkService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

//        GET    /api/works                 - Получить все произведения
//        GET    /api/works/{workId}        - Получить произведение по ID
//        GET    /api/works/search          - Фильтрация
//        GET    /api/works/{workId}/books  - Получить все книги произведения
//        POST   /api/works                 - Создать новое произведение
//  #-----      PUT    /api/works/{workId}        - Обновить произведение
//        DELETE /api/works/{workId}        - Удалить произведение


@RestController
@RequestMapping("/api/works")
public class WorkController {                       //это аннотация Spring, которая связывает HTTP-запрос
                                                    // (URL + метод) с конкретным методом Java-класса
                                                    //(контроллера).Метка в коде/инструкция

    private final WorkService workService;


    @Autowired
    public WorkController(WorkService workService) {
        this.workService = workService;
    }



//GET
    @GetMapping
    public ResponseEntity<?> getAllWorks() {
        try {
            List<WorkDTO> result = new LinkedList<>();
            for (Work work : workService.getAllWorks()) {
                result.add(new WorkDTO(work));
            }
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{workId}")
    public ResponseEntity<?> getWorkById(@PathVariable Long workId) {
        try {
            return ResponseEntity.ok(new WorkDTO(workService.getWorkById(workId)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getWorksByArguments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String writer,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Short year) {

        try {
            List<WorkDTO> result = new LinkedList<>();
            for (Work work : workService.getWorksByArguments(name, writer, genre, year)) {
                result.add(new WorkDTO(work));
            }
            return ResponseEntity.ok(result);                        //Возвращаем DTO с HTTP 200

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{workId}/books")
    public ResponseEntity<?> getBooksByWorkId(@PathVariable Long workId) {

        try {
            List<BookShortDTO> result = new LinkedList<>();
            for (Book book : workService.getBooksByWorkId(workId)) {
                result.add(new BookShortDTO(book));
            }
            return ResponseEntity.ok(result);                        //Возвращаем DTO с HTTP 200

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//POST
    @PostMapping
    public ResponseEntity<?> createWork(String name, String writer, String genre, Short year) {

        try {
            return ResponseEntity.ok(new WorkDTO(workService.createWork(name, writer, genre, year)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//PUT
//    @PutMapping("/{workId}")
//    public Work updateWork(@PathVariable Long workId, @RequestBody Work work) {
//        work.setId(workId);
//        return workService.updateWork(work);
//    }

//DELETE
    @DeleteMapping("/{workId}")
    @PreAuthorize("@mySecurity.isAdmin(authentication.principal.user)")
    public ResponseEntity<?> deleteWork(@PathVariable Long workId) {

        try {
            workService.deleteWorkLinkedData(workId);
            workService.deleteWork(workId);
            return ResponseEntity.ok("Successful");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }}

