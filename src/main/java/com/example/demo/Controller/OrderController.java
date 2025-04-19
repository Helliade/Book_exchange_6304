package com.example.demo.Controller;

import com.example.demo.Models.Order;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

//        GET    /api/orders                     - Получить все заказы
//        GET    /api/orders/{id}                - Получить заказ по ID
//   ?     POST   /api/orders                     - Создать новый заказ
//   ?     PUT    /api/orders/{id}                - Обновить заказ
//        PATCH  /api/orders/{id}/status         - Изменить статус заказа
//        DELETE /api/orders/{id}                - Удалить заказ
//   !!!!     POST   /api/orders/{id}/books          - Добавить книгу в заказ
//   !!!!     DELETE /api/orders/{id}/books/{bookId} - Удалить книгу из заказа
//   ?     GET    /api/orders?status=CREATED      - Фильтрация по статусу
//   ?     GET    /api/orders?type=TAKE           - Фильтрация по типу

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
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }


    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

//    @GetMapping
//    public ResponseEntity<List<Order>> getOrdersByStatus(
//            @RequestParam(required = false) String status,
//            @PageableDefault(size = 20) Pageable pageable) {
//
//        List<Order> orders = orderService.getOrdersByStatus(status, pageable);
//        return ResponseEntity.ok(orders);
//    }

//POST
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

//    @PostMapping("/{orderId}/books")
//    public ResponseEntity<Order> addBookToOrder(
//            @PathVariable Long orderId,
//            @RequestParam Long bookId) {
//
//        Order updatedOrder = orderService.addBookToOrder(orderId, bookId);
//        return ResponseEntity.ok(updatedOrder);
//    }

//PUT
    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        return orderService.updateOrder(order);
    }

//PATCH
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
        @PathVariable Long orderId,
        @RequestParam String status) {

        if (status == null || status.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status parameter cannot be blank"
            );
        }

        Order updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

//DELETE
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
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
