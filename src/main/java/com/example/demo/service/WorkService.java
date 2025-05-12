package com.example.demo.service;


import com.example.demo.Models.Order;
import com.example.demo.Models.Work;
import com.example.demo.repository.WorkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkService {

    private final WorkRepository workRepository;

    @Autowired
    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
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
    public Work getWorkById(Long id) {
        return workRepository.findById(id).orElse(null);
    }

    //Создание заказа
    public Work createWork(Work work) {
        return workRepository.save(work);
    }

    //Обновление заказа
    public Work updateWork(Work work) {
        return workRepository.save(work);
    }

    //Удаление заказа
    public void deleteWork(Long id) {
        workRepository.deleteById(id);
    }
}