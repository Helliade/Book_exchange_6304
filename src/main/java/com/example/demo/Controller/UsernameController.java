package com.example.demo.Controller;

import com.example.demo.Models.Username;
import com.example.demo.service.UsernameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsernameController {


    private final UsernameService usernameService;


    @Autowired
    public UsernameController(UsernameService usernameService) {
        this.usernameService = usernameService;
    }


    @GetMapping
    public List<Username> getAllUsernames() {
        return usernameService.getAllUsernames();
    }


    @GetMapping("/{id}")
    public Username getUsernameById(@PathVariable Long id) {
        return usernameService.getUsernameById(id);
    }


    @PostMapping("/register")
    public Username createUsername(@RequestBody Username username) {
        return usernameService.createUsername(username);
    }
//    // Эндпоинт для регистрации нового пользователя
//    @PostMapping
//    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {
//        try {
//            Username user = usernameService.registerUser(request.getLogin(), request.getPassword());
//            return ResponseEntity.ok(user);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

    @PutMapping("/{id}")
    public Username updateUsername(@PathVariable Long id, @RequestBody Username username) {
        username.setId(id);
        return usernameService.updateUsername(username);
    }


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
//    // DTO для смены пароля
//    public static class ChangePasswordRequest {
//        private String login;
//        private String oldPassword;
//        private String newPassword;
//
//        public ChangePasswordRequest() {
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
//        public String getOldPassword() {
//            return oldPassword;
//        }
//
//        public void setOldPassword(String oldPassword) {
//            this.oldPassword = oldPassword;
//        }
//
//        public String getNewPassword() {
//            return newPassword;
//        }
//
//        public void setNewPassword(String newPassword) {
//            this.newPassword = newPassword;
//        }
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