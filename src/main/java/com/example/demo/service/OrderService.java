package com.example.demo.service;

import com.example.demo.Models.Order;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    //Получение списка всех заказов
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    //Получение заказа по ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    //Создание заказа
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    //Обновление заказа
    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }

    //Удаление заказа
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}