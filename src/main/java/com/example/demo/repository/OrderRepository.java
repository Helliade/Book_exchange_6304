package com.example.demo.repository;

import com.example.demo.Models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
// Здесь можно добавить дополнительные методы для запросов к базе данных, если необходимо
}
