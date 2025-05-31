package com.example.demo.service;

import com.example.demo.Models.Book;
import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UsernameRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrderService {

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

//    public List<Order> getUserOrders(Long userId) {
//        // Проверяем существование пользователя
//        if (!usernameRepository.existsById(userId)) {
//            throw new EntityNotFoundException("User not found with id: " + userId);
//        }
//
//        List<Order> orders = orderRepository.findByUserId(userId);
//        if (orders.isEmpty()) {
//            throw new EntityNotFoundException("No orders found.");
//        }
//        return orders;
//    }

    //Получение списка всех заказов
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No orders found.");
        }
        return orders;
    }

    //Получение заказа по ID
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }

//    public Order getOrderWithBooksById(Long id) {
//        return orderRepository.findWithBooksById(id).orElse(null);
//    }
//
//    //Обновление заказа
//    public Order updateOrder(Order order) {
//        return orderRepository.save(order);
//    }

        //Создание заказа
    public Order createOrder(Username user) {
        Order order = new Order("GIVE", "CART", user);
        return orderRepository.save(order);
    }

    //Удаление заказа
    public void deleteOrder(Long orderId) {
        orderRepository.findById(orderId)                           //находим заказ
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        orderRepository.deleteById(orderId);
    }

    //Удаление связанных с заказом данных
    public void deleteOrderLinkedData(Long orderId) {
        Order order = orderRepository.findById(orderId)                           //находим заказ
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        // Удаляем связи из книг
        for (Book book : order.getBooks()) {
            book.getOrders().remove(order);
            bookRepository.save(book);
        }
    }

// при переходе из статуса корзины находится другая корзина или создается новая с тем же типом
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        try {
            OrderValidator.validateStatusTransition(order.getStatus(), newStatus);
            if ("CART".equals(order.getStatus())) {        //Создаем новый заказ со статусом корзины
                if ("TAKE".equals(order.getType())) {
                    for (Book book : order.getBooks()) {       //Книги переходят в статус Отсутствует, когда её забирают
                        book.setStatus("OUT");
                        bookRepository.save(book);
                    }
                } else {
                    for (Book book : order.getBooks()) {       //Книги переходят в статус Свободна, когда её отдают
                        book.setStatus("FREE");
                        bookRepository.save(book);
                    }
                }
                getCartByUserIdAndType(order.getUser().getId(), order.getType());
            }

            order.setStatus(newStatus);
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            // Обработка ошибок входящих параметров
            throw new IllegalArgumentException(e.getMessage());
        } catch (IllegalStateException e) {
            // Обработка нарушений бизнес-правил
            throw new IllegalStateException(e.getMessage());
        }
    }

    public Order updateOrderType(Long orderId, String newType) {
        // Валидация статуса
        if (OrderValidator.isValidType(newType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid type. Allowed values: " + OrderValidator.getValidTypes()
            );
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found with id: " + orderId
                ));

        if (!"CART".equals(order.getStatus())) {
            throw new IllegalStateException("Can't change the created order.");
        }

        order.setType(newType);
        return orderRepository.save(order);
    }

    @Transactional
    public Order addBookToOrder(Long orderId, Long bookId) {
        Order order = orderRepository.findById(orderId)                           //находим заказ
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        Book book = bookRepository.findById(bookId)                               //находим книгу
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (orderRepository.findBookIdsByBookingId(orderId).contains(bookId)) {   // Добавлена ли книга уже в заказ
            throw new IllegalStateException("Book already exists in the order");
        }

        if (!"FREE".equals(book.getStatus())) {                                   // Проверяем доступность книги
            throw new IllegalStateException("Book is not available. Status: " + book.getStatus());
        }

        if (!"CART".equals(order.getStatus())) {
            throw new IllegalStateException("Can't change the created order.");
        }

        book.setStatus("BOOKED");
        bookRepository.save(book); // НЕ Обновляем статус книги (т.к. она просто в корзине)

        // Добавляем книгу в заказ и сохраняем (обратная связь обновляется автоматически)
        order.getBooks().add(book);

        return orderRepository.save(order);
    }

    @Transactional
    public Order deleteBookFromOrder(Long orderId, Long bookId) {
        Order order = orderRepository.findById(orderId)                           //находим заказ
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        Book book = bookRepository.findById(bookId)                               //находим книгу
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));

        if (!orderRepository.findBookIdsByBookingId(orderId).contains(bookId)) {   // Есть ли книга в заказе
            throw new IllegalStateException("Book not exists in the order");
        }

        if (!"CART".equals(order.getStatus())) {
            throw new IllegalStateException("Can't change the created order.");
        }

        book.setStatus("FREE");
        bookRepository.save(book); // НЕ Обновляем статус книги (т.к. она просто в корзине)

        // Добавляем книгу в заказ и сохраняем (обратная связь обновляется автоматически)
        order.getBooks().remove(book);

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUserIdAndArguments(Long userId, String type, String status) { // Подразумевается,
        // что авторизированный пользователь выбирает на сайте один из возможных типов и/или статусов заказа
        // Поиск заказов
        List<Order> orders;
        if (type != null && status != null) {
            orders = orderRepository.findByUserIdAndTypeAndStatus(userId, type, status);
        } else if (type != null) {
            orders = orderRepository.findByUserIdAndType(userId, type);
        } else if (status != null) {
            orders = orderRepository.findByUserIdAndStatus(userId, status);
        } else {
            orders = orderRepository.findByUserId(userId);
        }

        // Проверка результатов
        if (orders.isEmpty()) {
            String mess = "No orders found.";
            if (type != null) mess += " Searched for type: " + type + ".";
            if (status != null) mess += " Searched for status: " + status + ".";
            throw new EntityNotFoundException(mess);
        }
        return orders;
    }

    //в любом случае вернется корзина нужного типа
    public Order getCartByUserIdAndType (Long userId, String type) {
        List<Order> orders = getOrdersByUserIdAndArguments(userId, type, "CART");
        Order order = new Order();
        if (orders.isEmpty()) {
            order = createOrder(usernameRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("User not found")));
            if (type.equals(order.getType())) {
                order.setType(type);
                orderRepository.save(order);
            }
        }
        return orders.set(0, order);
    }

    public List<Order> getOrdersByArguments(String type, String status) {

        // Проверка статуса и типа (если переданы)
        if (status != null && !OrderValidator.isValidStatus(status)) {
            throw new IllegalArgumentException(
                    String.format("Invalid status: %s. Valid statuses: %s", status, OrderValidator.getValidStatuses()));
        }
        if (type != null && !OrderValidator.isValidType(type)) {
            throw new IllegalArgumentException(
                    String.format("Invalid type: %s. Valid types: %s", type, OrderValidator.getValidTypes()));
        }

        // Поиск заказов
        List<Order> orders;
        if (type != null && status != null) {
            orders = orderRepository.findByTypeAndStatus(type, status);
        } else if (type != null) {
            orders = orderRepository.findByType(type);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else {
            throw new IllegalArgumentException(
                    "At least one parameter (type or status) must be provided");
        }

        // Проверка результатов
        if (orders.isEmpty()) {
            String mess = "No orders found.";
            if (type != null) mess += " Searched for type: " + type + ".";
            if (status != null) mess += " Searched for status: " + status + ".";
            throw new EntityNotFoundException(mess);
        }
        return orders;
    }
}





//        Username username = usernameRepository.findById(userId)                          // Проверяем, что пользователь
//                .orElseThrow(() -> new RuntimeException("Username not found"));          //существует
//
//        if (!VALID_STATUSES.contains(status)) {                                          // Проверяем, что статус
//            throw new IllegalArgumentException("Invalid order status: " + status);       //является допустимым значением
//        }