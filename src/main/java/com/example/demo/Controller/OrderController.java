package com.example.demo.Controller;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.Models.Order;
import com.example.demo.service.JwtService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UsernameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

//        GET    /api/orders                     - Получить все заказы
//        GET    /api/orders/{orderId}           - Получить заказ по ID
//        GET    /api/orders/search              - Фильтрация по типу и/или статусу (у пользователя в UsernameContr...)
//        POST   /api/orders/{userId}            - Создать новый заказ
//        (создается при переходе прошлого заказа из статуса корзины в созданный заказ)
//        POST   /api/orders/addBook             - Добавить книгу в заказ
//   ----     PUT    /api/orders/{id}                - Обновить заказ
//        PATCH  /api/orders/{orderId}/status    - Изменить статус заказа
//        PATCH  /api/orders/{orderId}/type      - Изменить тип заказа
//        DELETE /api/orders/{orderId}           - Удалить заказ
//        DELETE /api/orders/{orderId}/books     - Удалить книгу из заказа


@RestController
@EnableMethodSecurity(prePostEnabled = true)
@RequestMapping("/api/orders")                                             //это аннотация Spring, которая связывает HTTP-запрос
                                                                           // (URL + метод) с конкретным методом Java-класса
                                                                           //(контроллера).Метка в коде/инструкция
public class OrderController {


    private final OrderService orderService;
    private final UsernameService usernameService;
    private final JwtService jwtService;


    @Autowired
    public OrderController(OrderService orderService, UsernameService usernameService, JwtService jwtService) {

        this.orderService = orderService;
        this.usernameService = usernameService;
        this.jwtService = jwtService;
    }

//GET
    @GetMapping
    @PreAuthorize("@mySecurity.isAdmin(authentication.principal.user)")
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

    //TODO может переписать, чтобы ID пользователя брался через токен и проверялось, его ли это заказ?
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(new OrderDTO(orderService.getOrderById(orderId)));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search")                                                 //только админ может вызывать
    @PreAuthorize("@mySecurity.isAdmin(authentication.principal.user)")
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
    @PostMapping("/{userId}")
    public ResponseEntity<?> createOrder(@PathVariable Long userId) {
        return ResponseEntity.ok(new OrderDTO(orderService.createOrder(usernameService.getUsernameById(userId))));
    }

    @PostMapping("/addBook")
    public ResponseEntity<?> addBookToOrder(
            @RequestParam Long bookId,
            @RequestParam String typeOfOrder,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String accessToken = authHeader.substring(7);
            Order order = orderService.getCartByUserIdAndType
                    (jwtService.extractUsernameModel(accessToken).getId(), typeOfOrder);
            Order updatedOrder = orderService.addBookToOrder(order.getId(), bookId);
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
    //TODO проверка на то, что пользователь может лишь оформить и отменить заказ
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
    @PreAuthorize("@mySecurity.isAdmin(authentication.principal.user)")
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