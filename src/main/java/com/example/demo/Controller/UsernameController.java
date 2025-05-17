package com.example.demo.Controller;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.UsernameDTO;
import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import com.example.demo.service.OrderService;
import com.example.demo.service.UsernameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public UsernameController(UsernameService usernameService, OrderService orderService) {
        this.usernameService = usernameService;
        this.orderService = orderService;
    }

//GET
    @GetMapping
    public List<UsernameDTO> getAllUsernames() {
        List<UsernameDTO> result = new LinkedList<>();
        for (Username username : usernameService.getAllUsernames()) {
            result.add(new UsernameDTO(username));
        }
        return result;
    }

    @GetMapping("/{userId}")
    public UsernameDTO getUsernameById(@PathVariable Long userId) {
        return new UsernameDTO(usernameService.getUsernameById(userId));
    }

//    @GetMapping("/{userId}/orders")
//    public ResponseEntity<?> getUserOrders(
//            @PathVariable Long userId,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) String type) {
//
//        try {
//            List<OrderDTO> result = new LinkedList<>();
//            for (Order order : orderService.getUserOrders(userId)) {
//                result.add(new OrderDTO(order));
//            }
//            return ResponseEntity.ok(result);                        //Возвращаем DTO с HTTP 200
//
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//    }

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
    public ResponseEntity<?> createUsername(String login, String rawPassword) {
        try {
            Username user = usernameService.registerUsername(login, rawPassword);
            return ResponseEntity.ok(new UsernameDTO(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(String login, String rawPassword) {
        boolean isAuthenticated = usernameService.authenticateUsername(login, rawPassword);
        if (isAuthenticated) {
            return ResponseEntity.ok("Authentication successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

//PUT

//    //TODO переписать метод обновления пользователя
//    @PutMapping("/{userId}")
//    public Username updateUsername(@PathVariable Long userId, @RequestBody Username username) {
//        username.setId(userId);
//        return usernameService.updateUsername(username);
//    }

//    @PutMapping("/{id}")
//    public ResponseEntity<UsernameDTO> updateUsername(
//            @PathVariable Long id,
//            @Valid @RequestBody UpdateUserRequest updateRequest) {
//
//        UsernameDTO updatedUser = usernameService.updateUser(id, updateRequest);
//        return ResponseEntity.ok(updatedUser);
//    }


    //DELETE
    //TODO удалить все связанные данные
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUsername(@PathVariable Long userId) {

        try {
            usernameService.deleteUsername(userId);
            return ResponseEntity.ok("Successful");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}






//import com.example.demo.Models.Username;
//import com.example.demo.service.UsernameService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/users")
//public class UsernameController {
//
//    private final UsernameService usernameService;
//
//    public UsernameController(UsernameService usernameService) {
//        this.usernameService = usernameService;
//    }
//
//    // Эндпоинт для регистрации нового пользователя
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
//        try {
//            Username user = usernameService.registerUser(request.getLogin(), request.getPassword());
//            return ResponseEntity.ok(user);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//
//    // Эндпоинт для аутентификации пользователя
//    @PostMapping("/authenticate")
//    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest request) {
//        boolean isAuthenticated = usernameService.authenticate(request.getLogin(), request.getPassword());
//        if (isAuthenticated) {
//            return ResponseEntity.ok("Authentication successful");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }
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
//
//    // DTO для регистрации
//    public static class RegistrationRequest {
//        private String login;
//        private String password;
//
//        public RegistrationRequest() {
//        }
//
//        public String getLogin() {
//            return login;
//        }
//
//        public void setLogin(String login) {
//            this.login = login;
//        }
//
//        public String getPassword() {
//            return password;
//        }
//
//        public void setPassword(String password) {
//            this.password = password;
//        }
//    }
//
//    // DTO для аутентификации
//    public static class AuthenticationRequest {
//        private String login;
//        private String password;
//
//        public AuthenticationRequest() {
//        }
//
//        public String getLogin() {
//            return login;
//        }
//
//        public void setLogin(String login) {
//            this.login = login;
//        }
//
//        public String getPassword() {
//            return password;
//        }
//
//        public void setPassword(String password) {
//            this.password = password;
//        }
//    }
//

//    @PostMapping("/register")
//    public Username createUsername(@RequestBody Username username) {
//        return usernameService.registerUsername(username);
//    }
//}