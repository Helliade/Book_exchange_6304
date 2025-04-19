package com.example.demo.Controller;

import com.example.demo.Models.Work;
import com.example.demo.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//        GET    /api/works             - Получить все произведения
//        GET    /api/works/{id}        - Получить произведение по ID
//        POST   /api/works             - Создать новое произведение
//        PUT    /api/works/{id}        - Обновить произведение
//        DELETE /api/works/{id}        - Удалить произведение
//  ?      GET    /api/works?genre=Novel - Фильтрация по жанру
//  ?      GET    /api/works/search?q=   - Поиск по названию/автору
//  !!!!      GET    /api/works/{id}/books  - Получить все книги произведения

@RestController
@RequestMapping("/api/works")
public class WorkController {


    private final WorkService workService;


    @Autowired
    public WorkController(WorkService workService) {
        this.workService = workService;
    }

//GET
    @GetMapping
    public List<Work> getAllWorks() {
        return workService.getAllWorks();
    }


    @GetMapping("/{id}")
    public Work getWorkById(@PathVariable Long id) {
        return workService.getWorkById(id);
    }

//POST
    @PostMapping
    public Work createWork(@RequestBody Work work) {
        return workService.createWork(work);
    }

//PUT
    @PutMapping("/{id}")
    public Work updateWork(@PathVariable Long id, @RequestBody Work work) {
        work.setId(id);
        return workService.updateWork(work);
    }

//DELETE
    @DeleteMapping("/{id}")
    public void deleteWork(@PathVariable Long id) {
        workService.deleteWork(id);
    }
}

