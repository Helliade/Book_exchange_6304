package com.example.demo.Controller;

import com.example.demo.DTO.UsernameDTO;
import com.example.demo.Models.Username;
import com.example.demo.service.JwtService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UsernameService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final UsernameService usernameService;
    private final OrderService orderService;
    private final UserDetailsService userDetailsService;

    public record Request(String login, String rawPassword){}
    public record ChangePasswordRequest(String login, String rawPassword, String newPassword) {}

    public AuthenticationController(JwtService jwtService, UsernameService usernameService, OrderService orderService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.usernameService = usernameService;
        this.orderService = orderService;
        this.userDetailsService = userDetailsService;
    }

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

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody Request request) {

        try {
            Username user = new Username(request.login, request.rawPassword);
            String accessToken = usernameService.verify(user);

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.login);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            return ResponseEntity.ok(new com.example.demo.Models.UserModel(accessToken, refreshToken, user.getRole()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        try {
            // Проверяем старый access token
            String oldAccessToken = authHeader.substring(7);

            // Проверяем refresh token
            if (!jwtService.validateRefreshToken(refreshToken)) {
                // Перенаправляем на страницу авторизации
                jwtService.invalidateToken(refreshToken);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "error", "Invalid refresh token",
                                "redirect_url", "/api/auth/login"
                        ));
            }

            // Добавляем старые токены в blacklist
            jwtService.invalidateToken(oldAccessToken);

            // Генерируем новые токены
            String login = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(login);

            return ResponseEntity.ok(Map.of(
                    "accessToken", jwtService.generateAccessToken(userDetails),
                    "refreshToken", refreshToken,
                    "expires_in", jwtService.getRemainingTokenExpiration(refreshToken) / 1000
            ));
        } catch (Exception e) {
//            throw new RuntimeException("Redirection failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Эндпоинт для смены пароля
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            usernameService.changePassword(request.login, request.rawPassword, request.newPassword);
            return ResponseEntity.ok("Password changed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        String accessToken = authHeader.substring(7);

        // Добавляем токены в blacklist
        jwtService.invalidateToken(accessToken);
        jwtService.invalidateToken(refreshToken);

        return ResponseEntity.ok().build();
    }



    //POST

//    @PostMapping("/register")
//    public ResponseEntity<?> createUsername(@RequestParam String login, @RequestParam String rawPassword) {
//        try {
//            Username user = usernameService.registerUsername(login, rawPassword);
//            orderService.createOrder(user);   //создаем первый заказ со статусом корзины
//            return ResponseEntity.ok(new UsernameDTO(user));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }

//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestParam String login, @RequestParam String rawPassword) {
//
//        try {
//            Username user = new Username(login, rawPassword);
//            String token = usernameService.verify(user);
//
//            return ResponseEntity.ok(new com.example.demo.Models.UserModel(token, user.getRole()));
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
//    }



//    // Эндпоинт для смены пароля
//    @PostMapping("/change-password")
//    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
//        try {
//            usernameService.changePassword(oldPassword, newPassword);
//            return ResponseEntity.ok("Password changed successfully");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        }
//    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String jwt = authHeader.substring(7);
//
//            // Получаем время истечения токена
//            Date expiration = jwtService.extractExpiration(jwt);
//
//            // Добавляем токен в черный список
//            tokenUsageService.markTokenAsUsed(jwt, expiration);
//
//            return ResponseEntity.ok("Logout successful");
//        }
//
//        return ResponseEntity.badRequest().body("Invalid token");
//    }

}
