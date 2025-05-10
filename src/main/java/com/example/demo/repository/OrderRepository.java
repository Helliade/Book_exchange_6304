package com.example.demo.repository;

import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
//  findAll() и findById() определены автоматически

// Находит все заказы по пользователю (Username)
    List<Order> findByUser(Username user);
    List<Order> findByType(String type);
    List<Order> findByStatus(String status);

    @EntityGraph(attributePaths = {"books", "user"}) // Для избежания N+1 проблемы
    List<Order> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"books", "user"})
    List<Order> findByUserIdAndStatus(Long userId, String status);

    @EntityGraph(attributePaths = {"books", "user"})
    List<Order> findByUserIdAndType(Long userId, String type);

    @EntityGraph(attributePaths = {"books", "user"})
    List<Order> findByUserIdAndTypeAndStatus(Long userId, String type, String status);

    @EntityGraph(attributePaths = {"books", "user"})
    List<Order> findByTypeAndStatus(String type, String status);

//    @EntityGraph(attributePaths = {"books", "user"}) // Жадная загрузка связанных сущностей
//    List<Order> findByStatus(String status, Pageable pageable);

    @Query(value = """
        SELECT bb.Book_id
        FROM Booking_Book bb
        WHERE bb.Booking_id = :orderId
        """, nativeQuery = true)
    List<Long> findBookIdsByBookingId(@Param("orderId") Long orderId);

    @EntityGraph(attributePaths = "books")
    Optional<Order> findWithBooksById(Long id);

//    @Modifying
//    @Transactional
//    @Query(value = "INSERT INTO Booking_Book (Booking_id, Book_id) VALUES (:orderId, :bookId)",
//            nativeQuery = true)
//    void addBookToOrder(@Param("orderId") Long orderId, @Param("bookId") Long bookId);
}
