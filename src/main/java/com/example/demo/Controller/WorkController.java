package com.example.demo.Controller;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.WorkDTO;
import com.example.demo.Models.Order;
import com.example.demo.Models.Work;
import com.example.demo.service.WorkService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

//  #      GET    /api/works             - Получить все произведения
//  #      GET    /api/works/{workId}        - Получить произведение по ID
//  #      POST   /api/works             - Создать новое произведение
//  #      PUT    /api/works/{workId}        - Обновить произведение
//  #      DELETE /api/works/{workId}        - Удалить произведение
//  #?      GET    /api/works?genre=Novel - Фильтрация по жанру
//  #?      GET    /api/works/search?q=   - Поиск по названию/автору
//  #!!!!      GET    /api/works/{workId}/books  - Получить все книги произведения

@RestController
@RequestMapping("/api/works")                                                 //это аннотация Spring, которая связывает HTTP-запрос
                                                                              // (URL + метод) с конкретным методом Java-класса
                                                                              //(контроллера).Метка в коде/инструкция
public class WorkController {


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

//POST
    @PostMapping
    public Work createWork(@RequestBody Work work) {
        return workService.createWork(work);
    }

//PUT
    @PutMapping("/{workId}")
    public Work updateWork(@PathVariable Long workId, @RequestBody Work work) {
        work.setId(workId);
        return workService.updateWork(work);
    }

//DELETE
    @DeleteMapping("/{workId}")
    public void deleteWork(@PathVariable Long workId) {
        workService.deleteWork(workId);
    }
}

