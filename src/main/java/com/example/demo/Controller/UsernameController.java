package com.example.demo.Controller;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.UsernameDTO;
import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import com.example.demo.service.JwtService;
import com.example.demo.service.TokenUsageService;
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

//        GET    /api/users                   - Получить всех пользователей
//        GET    /api/users/{userId}          - Получить пользователя по ID
//        GET    /api/users/search   - Фильтрация заказов по типу и/или статусу у определенного пользователя
//                                            + Вывод всех заказов
//   #-----     PUT/PATCH    /api/users/{userId}           - Обновить пользователя
//        DELETE /api/users/{userId}          - Удалить пользователя


@RestController
@EnableMethodSecurity(prePostEnabled = true)
@RequestMapping("/api/users")                                           //это аннотация Spring, которая связывает HTTP-запрос
                                                                        // (URL + метод) с конкретным методом Java-класса
                                                                        //(контроллера).Метка в коде/инструкция
public class UsernameController {

    private final UsernameService usernameService;
    private final OrderService orderService;
    private final JwtService jwtService;

    @Autowired
    public UsernameController(UsernameService usernameService, OrderService orderService, JwtService jwtService) {
        this.usernameService = usernameService;
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

//GET
    @GetMapping
    @PreAuthorize("@mySecurity.isAdmin(authentication.principal.user)")
    public List<UsernameDTO> getAllUsernames() {
        List<UsernameDTO> result = new LinkedList<>();
        for (Username username : usernameService.getAllUsernames()) {
            result.add(new UsernameDTO(username));
        }
        return result;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("@mySecurity.isAdmin(authentication.principal.user)")
    public UsernameDTO getUsernameById(@PathVariable Long userId) {
        return new UsernameDTO(usernameService.getUsernameById(userId));
    }

    //TODO может переписать, чтобы ID брался через токен?
    @GetMapping("/search")
    public ResponseEntity<?> getOrdersByUserIdAndArguments(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.substring(7); // Убираем "Bearer "
            Long userId = jwtService.extractUsernameModel(token).getId();
            List<OrderDTO> result = new LinkedList<>();
            for (Order order : orderService.getOrdersByUserIdAndArguments(userId, type, status)) {
                result.add(new OrderDTO(order));
            }
            return ResponseEntity.ok(result);                        //Возвращаем DTO с HTTP 200

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка сервера: " + e.getMessage());
        }
    }

//PUT

//    //переписать метод обновления пользователя
//    @PutMapping("/{userId}")
//    public Username updateUsername(@PathVariable Long userId, @RequestBody Username username) {
//        username.setId(userId);
//        return usernameService.updateUsername(username);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<UsernameDTO> updateUsername(
//            @PathVariable Long id,
//            @Valid @RequestBody UpdateUserRequest updateRequest) {
//
//        UsernameDTO updatedUser = usernameService.updateUser(id, updateRequest);
//        return ResponseEntity.ok(updatedUser);
//    }


    //DELETE

    @DeleteMapping("/{userId}")
    @PreAuthorize("@mySecurity.isAdmin(authentication.principal.user)")
    public ResponseEntity<?> deleteUsername(@PathVariable Long userId) {

        try {
            usernameService.deleteUsernameLinkedData(userId);
            usernameService.deleteUsername(userId);
            return ResponseEntity.ok("Successful");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}