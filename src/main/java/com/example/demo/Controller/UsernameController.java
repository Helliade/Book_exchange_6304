package com.example.demo.Controller;

import com.example.demo.DTO.ChangePasswordDTO;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.UsernameDTO;
import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import com.example.demo.config.JwtService;
import com.example.demo.config.TokenUsageService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UsernameService;
import com.example.demo.models.UserModel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//        GET    /api/users                   - Получить всех пользователей
//        GET    /api/users/{userId}          - Получить пользователя по ID
//        GET    /api/users/{userId}/search   - Фильтрация заказов по типу и/или статусу у определенного пользователя
//                                            + Вывод всех заказов
//        POST   /api/users/register          - Создать нового пользователя
//        POST   /api/users/login             - Аутентификация пользователя
//   #-----     PUT/PATCH    /api/users/{userId}           - Обновить пользователя
//        DELETE /api/users/{userId}          - Удалить пользователя


@RestController

@RequestMapping("/api/users")                                           //это аннотация Spring, которая связывает HTTP-запрос
                                                                        // (URL + метод) с конкретным методом Java-класса
                                                                        //(контроллера).Метка в коде/инструкция
public class UsernameController {

    private final UsernameService usernameService;
    private final OrderService orderService;
    private final JwtService jwtService;
    private final TokenUsageService tokenUsageService;

    @Autowired
    public UsernameController(UsernameService usernameService, OrderService orderService, JwtService jwtService, TokenUsageService tokenUsageService) {
        this.usernameService = usernameService;
        this.orderService = orderService;
        this.jwtService = jwtService;
        this.tokenUsageService = tokenUsageService;
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

    @GetMapping("/{userId}/search")
    public ResponseEntity<?> getOrdersByUserIdAndArguments(
            @PathVariable Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        try {
            List<OrderDTO> result = new LinkedList<>();
            for (Order order : orderService.getOrdersByUserIdAndArguments(userId, type, status)) {
                result.add(new OrderDTO(order));
            }
            return ResponseEntity.ok(result);                        //Возвращаем DTO с HTTP 200

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//POST

    @PostMapping("/register")
    public ResponseEntity<?> createUsername(@RequestParam String login, @RequestParam String rawPassword) {
        try {
            Username user = usernameService.registerUsername(login, rawPassword);
            orderService.createOrder(user);   //создаем первый заказ со статусом корзины
            return ResponseEntity.ok(new UsernameDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //TODO проверить!!!
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestParam String login, @RequestParam String rawPassword) {

        try {
            Username user = new Username(login, rawPassword);
            String token = usernameService.verify(user);

            return ResponseEntity.ok(new UserModel(token, user.getRole()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }



    // Эндпоинт для смены пароля
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO request) {
        try {
            usernameService.changePassword(request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")   //TODO странный метод...
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            // Получаем время истечения токена
            Date expiration = jwtService.extractExpiration(jwt);

            // Добавляем токен в черный список
            tokenUsageService.markTokenAsUsed(jwt, expiration);

            return ResponseEntity.ok("Logout successful");
        }

        return ResponseEntity.badRequest().body("Invalid token");
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


//
//    // Эндпоинт для смены пароля
//    @PostMapping("/change-password")
//    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
//        try {
//            usernameService.changePassword(request.getLogin(), request.getOldPassword(), request.getNewPassword());
//            return ResponseEntity.ok("Password changed successfully");
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//}