package com.example.demo.Controller;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.Models.Order;
import com.example.demo.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

//        GET    /api/orders                     - Получить все заказы
//        GET    /api/orders/{userId}            - Получить заказ по ID пользователя
//        GET    /api/orders/search              - Фильтрация по типу и/или статусу (у пользователя в UsernameContr...)
//   ???#     POST   /api/orders                     - Создать новый заказ
//        POST   /api/orders/{id}/books          - Добавить книгу в заказ
//   ?#     PUT    /api/orders/{id}                - Обновить заказ
//        PATCH  /api/orders/{id}/status         - Изменить статус заказа
//   #     DELETE /api/orders/{id}                - Удалить заказ
//   !!!!#     DELETE /api/orders/{id}/books - Удалить книгу из заказа


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

//GET
    @GetMapping
    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> result = new LinkedList<>();
        for (Order order : orderService.getAllOrders()) {
            result.add(new OrderDTO(order));
        }
        return result;
    }


    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable Long id) {
        return new OrderDTO(orderService.getOrderById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<?> getOrdersByTypeAndStatus(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        try {
            List<OrderDTO> result = new LinkedList<>();
            for (Order order : orderService.getOrdersByTypeAndStatus(type, status)) {
                result.add(new OrderDTO(order));
            }
            return ResponseEntity.ok(result);                        //Возвращаем DTO с HTTP 200

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//POST

// TODO Передаем не сущность, а поля
// Достаем из бд сущности книг и пользователей с необходимыми айди (или создаем?)
// Создаем сами экземпляр заказа и записываем данные в присоединенные таблицы

//Создание заказа - передаем сущность заказа со статусом корзины,
//переводим статус в Создан, создаем новый заказ со статусом корзины
    //TODO передать DTO
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        orderService.updateOrderStatus(order.getId(), "CREATED");
        Order newOrder = new Order("GIVE", "CART", order.getUser());
        return orderService.createOrder(newOrder);
    }

    @PostMapping(value = "/{orderId}/books")
    public ResponseEntity<?> addBookToOrder(
            @PathVariable Long orderId,
            @RequestParam Long bookId) {

        try {
            Order updatedOrder = orderService.addBookToOrder(orderId, bookId);
            return ResponseEntity.ok(new OrderDTO(updatedOrder));                        //Возвращаем DTO с HTTP 200

        } catch (IllegalStateException e) {
            // Обработка ошибки (книга уже в заказе)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            // Обработка ошибки (заказ или книга не найдены)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//PUT
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        return orderService.updateOrder(order);
    }

//PATCH
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
        @PathVariable Long orderId,
        @RequestParam String status) {

        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(new OrderDTO(updatedOrder));                        //Возвращаем DTO с HTTP 200

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {

        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Successful");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    //TODO передать DTO
    @DeleteMapping(value = "/{id}/books")
    public ResponseEntity<?> deleteBookFromOrder(
            @PathVariable Long orderId,
            @RequestParam Long bookId) {

        try {
            Order updatedOrder = orderService.deleteBookFromOrder(orderId, bookId);
            return ResponseEntity.ok(new OrderDTO(updatedOrder));                        //Возвращаем DTO с HTTP 200

        } catch (IllegalStateException e) {
            // Обработка ошибки (книги нет в заказе)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            // Обработка ошибки (заказ или книга не найдены)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}



//    @PostMapping
//    public ResponseEntity<Booking> create(@RequestBody Booking booking,
//                                          @RequestParam Long userId) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(service.create(booking, userId));
//    }
//
//    @PostMapping("/{bookingId}/books")
//    public ResponseEntity<Booking> addBook(@PathVariable Long bookingId,
//                                           @RequestParam Long bookId) {
//        return ResponseEntity.ok(service.addBookToBooking(bookingId, bookId));
//    }
//
//    @PatchMapping("/{id}/status")
//    public ResponseEntity<Booking> updateStatus(@PathVariable Long id,
//                                                @RequestBody Map<String, String> status) {
//        return ResponseEntity.ok(service.updateStatus(id, status.get("status")));
//    }
