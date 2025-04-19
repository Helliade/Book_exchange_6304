package com.example.demo.repository;

import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
//  findAll() и findById() определены автоматически

// Находит все заказы по пользователю (Username)
    List<Order> findByUser(Username user);
    List<Order> findByType(String type);

    @EntityGraph(attributePaths = {"books", "user"}) // Для избежания N+1 проблемы
    List<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"books", "user"})
    List<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);

    @EntityGraph(attributePaths = {"books", "user"})
    List<Order> findByUserIdAndType(Long userId, String type, Pageable pageable);

    @EntityGraph(attributePaths = {"books", "user"})
    List<Order> findByUserIdAndStatusAndType(Long userId, String status, String type, Pageable pageable);

    @EntityGraph(attributePaths = {"books", "user"}) // Жадная загрузка связанных сущностей
    List<Order> findByStatus(String status, Pageable pageable);
}
