package com.example.demo.service;

import com.example.demo.Models.Book;
import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UsernameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    private static final Set<String> VALID_STATUSES = Set.of(  // Хочу добавить BASKET_MODE
            "CREATED",
            "IN_TRANSIT",
            "DELIVERY_READY",
            "COMPLETED"
    );

    private final OrderRepository orderRepository;
    private final UsernameRepository usernameRepository;

    private final BookRepository bookRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UsernameRepository usernameRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.usernameRepository = usernameRepository;
        this.bookRepository = bookRepository;
    }

    public List<Order> getUserOrders(Long userId, String status, String type, Pageable pageable) {
        // Проверяем существование пользователя
        if (!usernameRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found with id: " + userId
            );
        }

        // Строим запрос в зависимости от параметров
        if (status != null && type != null) {
            return orderRepository.findByUserIdAndStatusAndType(userId, status, type, pageable);
        } else if (status != null) {
            return orderRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (type != null) {
            return orderRepository.findByUserIdAndType(userId, type, pageable);
        } else {
            return orderRepository.findByUserId(userId, pageable);
        }
    }

    //Получение списка всех заказов
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUser(Username user) {
        return orderRepository.findByUser(user);
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

//    public List<Order> getOrdersByStatus(String status, Pageable pageable) {
//        if (status != null) {
//            // Валидация статуса
//            if (!VALID_STATUSES.contains(status)) {
//                throw new ResponseStatusException(
//                        HttpStatus.BAD_REQUEST,
//                        "Invalid status. Allowed values: " + VALID_STATUSES
//                );
//            }
//            return orderRepository.findByStatus(status, pageable);
//        }
//        return orderRepository.findAll(pageable).getContent();
//    }

    public Order updateOrderStatus(Long orderId, String newStatus) {
        // Валидация статуса
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status. Allowed values: " + VALID_STATUSES
            );
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found with id: " + orderId
                ));

        //TODO Проверка допустимости перехода статусов
//        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

//    public Order addBookToOrder(Long orderId, Long bookId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND,
//                        "Order not found with id: " + orderId
//                ));
//
//        Book book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND,
//                        "Book not found with id: " + bookId
//                ));
//
//        // Проверяем, не добавлена ли книга уже в заказ
//        if (orderRepository.findBookIdsByBookingId(orderId).contains(bookId)) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Book with id " + bookId + " already exists in order"
//            );
//        }
//
//        // Проверяем доступность книги
//        if (!"FREE".equals(book.getStatus())) {
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_REQUEST,
//                    "Book with id " + bookId + " is not available. Status: " + book.getStatus()
//            );
//        }
//
//        orderRepository.addBook(orderId, bookId);
//        book.setStatus("BOOKED"); // Обновляем статус книги
//
//        return orderRepository.save(order);
//    }

//    public Set<Book> getBooksInOrder(Long orderId) {
//        Order order = orderRepository.findByIdWithBooks(orderId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND,
//                        "Order not found with id: " + orderId
//                ));
//
//        return order.getBooks();
//    }
}