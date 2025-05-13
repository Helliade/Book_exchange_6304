package com.example.demo.Controller;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.Models.Order;
import com.example.demo.service.OrderService;
import com.example.demo.service.UsernameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

//        GET    /api/orders                     - Получить все заказы
//        GET    /api/orders/{orderId}           - Получить заказ по ID
//        GET    /api/orders/search              - Фильтрация по типу и/или статусу (у пользователя в UsernameContr...)
//        POST   /api/orders/{userId}            - Создать новый заказ
//        (создается при переходе прошлого заказа из статуса корзины в созданный заказ)
//        POST   /api/orders/{orderId}/books     - Добавить книгу в заказ
//   ----     PUT    /api/orders/{id}                - Обновить заказ
//        PATCH  /api/orders/{orderId}/status    - Изменить статус заказа
//        PATCH  /api/orders/{orderId}/type      - Изменить тип заказа
//        DELETE /api/orders/{orderId}           - Удалить заказ
//        DELETE /api/orders/{orderId}/books     - Удалить книгу из заказа


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UsernameService usernameService;

    @Autowired
    public OrderController(OrderService orderService, UsernameService usernameService) {
        this.orderService = orderService;
        this.usernameService = usernameService;
    }

//GET
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderDTO> result = new LinkedList<>();
            for (Order order : orderService.getAllOrders()) {
                result.add(new OrderDTO(order));
            }
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(new OrderDTO(orderService.getOrderById(orderId)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> getOrdersByArguments(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        try {
            List<OrderDTO> result = new LinkedList<>();
            for (Order order : orderService.getOrdersByArguments(type, status)) {
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
    //Создание заказа - передаем сущность заказа со статусом корзины,
    //переводим статус в Создан, создаем новый заказ со статусом корзины
    //TODO
    @PostMapping("/{userId}")
    public ResponseEntity<?> createOrder(@PathVariable Long userId) {
        return ResponseEntity.ok(new OrderDTO(orderService.createOrder(usernameService.getUsernameById(userId))));
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
//    @PutMapping("/{id}")
//    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
//        order.setId(id);
//        return orderService.updateOrder(order);
//    }

//PATCH
    //TODO может после обновления попробовать поискать заказ в статусе корзины (найдется лишний)
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

    @PatchMapping("/{orderId}/type")
    public ResponseEntity<?> updateOrderType(
            @PathVariable Long orderId,
            @RequestParam String type) {

        try {
            Order updatedOrder = orderService.updateOrderType(orderId, type);
            return ResponseEntity.ok(new OrderDTO(updatedOrder));                        //Возвращаем DTO с HTTP 200

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//DELETE

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {

        try {
            orderService.deleteOrderLinkedData(orderId);
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Successful");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping(value = "/{orderId}/books")
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

