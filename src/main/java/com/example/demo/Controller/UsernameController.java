package com.example.demo.Controller;

import com.example.demo.DTO.UsernameDTO;
import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import com.example.demo.service.OrderService;
import com.example.demo.service.UsernameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

//        GET    /api/users                - Получить всех пользователей
//        GET    /api/users/{id}           - Получить пользователя по ID
//   ?     POST   /api/users                - Создать нового пользователя ??????
//        POST   /api/users/register       - Создать нового пользователя
//   ?     PUT    /api/users/{id}           - Обновить пользователя
//        DELETE /api/users/{id}           - Удалить пользователя
//        GET    /api/users/{id}/bookings  - Получить все бронирования пользователя
//        POST   /api/users/login          - Аутентификация пользователя

@RestController
@RequestMapping("/api/users")
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
    public List<Username> getAllUsernames() {
        return usernameService.getAllUsernames();
    }

    @GetMapping("/{id}")
    public UsernameDTO getUsernameById(@PathVariable Long id) {
        return usernameService.getUsernameById(id);
    }

    @GetMapping("/{userId}/orders")    // А точно ли тут???
    public ResponseEntity<List<Order>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 10) Pageable pageable) {
        List<Order> orders = orderService.getUserOrders(userId, status, type, pageable);
        return ResponseEntity.ok(orders);
    }

//POST

    @PostMapping("/register")    //я не понимаю, как я буду работать в системе, если не знаю свой ID
    public ResponseEntity<?> createUsername(@RequestBody Username username) {
        try {
            UsernameDTO user = usernameService.registerUsername(username);
            return ResponseEntity.ok(user);
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

    //TODO переписать метод обновления пользователя
    @PutMapping("/{id}")
    public Username updateUsername(@PathVariable Long id, @RequestBody Username username) {
        username.setId(id);
        return usernameService.updateUsername(username);
    }
//    @PutMapping("/{id}")
//    public ResponseEntity<UsernameDTO> updateUsername(
//            @PathVariable Long id,
//            @Valid @RequestBody UpdateUserRequest updateRequest) {
//
//        UsernameDTO updatedUser = usernameService.updateUser(id, updateRequest);
//        return ResponseEntity.ok(updatedUser);
//    }


    //DELETE
    @DeleteMapping("/{id}")
    public void deleteUsername(@PathVariable Long id) {
        usernameService.deleteUsername(id);
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


//Пояснения
//   Путь "/users": базовый URL для данного контроллера. Вы можете изменить его в зависимости от ваших предпочтений.
//   Метод /register принимает JSON с полями login и password и передаёт их в метод registerUser вашего сервиса.
// При возникновении ошибки (например, если пользователь с таким логином уже существует) возвращается BAD_REQUEST с
// сообщением ошибки.
//   Метод /authenticate принимает данные для аутентификации (логин и пароль) и проверяет их с помощью метода
// authenticate. В случае неверных данных возвращается статус UNAUTHORIZED.
//   Метод /change-password принимает логин, старый пароль и новый пароль, выполняет аутентификацию и, если всё
// верно, обновляет пароль пользователя.