package com.example.demo.service;

import com.example.demo.Models.Order;
import com.example.demo.Models.Username;
import com.example.demo.config.JwtService;
import com.example.demo.repository.UsernameRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UsernameService {

    private final UsernameRepository usernameRepository;
    private final OrderService orderService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public UsernameService(UsernameRepository usernameRepository, OrderService orderService, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usernameRepository = usernameRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderService = orderService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    //Получение списка всех пользователей
    public List<Username> getAllUsernames() {
        return usernameRepository.findAll();
    }

    public Username getUsernameById(Long id) {
        return usernameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Username not found"));
    }

//    public Username getUserByLogin(String login) {
//        return usernameRepository.findByLogin(login)
//                .orElseThrow(() -> new RuntimeException("Username not found"));
//    }



    @Transactional
    public Username registerUsername(String login, String password) {
        // Проверка существования пользователя
        if (usernameRepository.findByLogin(login) != null) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }

        // Создание нового пользователя
        Username username = new Username(login, passwordEncoder.encode(password)); // Хеширование пароля
        return usernameRepository.save(username);
    }

    public String verify(Username username) {
        Authentication authenticate
                = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username.getLogin(), username.getPassword()
                )
        );

//        var u = userRepository.findByLogin(user.getLogin());
        if(!authenticate.isAuthenticated())
            throw new EntityNotFoundException("Invalid login or password"); // Пользователь не найден
        return jwtService.generateToken(username);
    }


//    public Username authenticateUsername(String login, String rawPassword) { //String login, String rawPassword) {
//        // Поиск пользователя по логину
//        Username user = usernameRepository.findByLogin(login);
//        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
//            throw new EntityNotFoundException("Invalid login or password"); // Пользователь не найден
//        }
//        return user;
//    }


//    // Обновление пользователя   !!!!
//    public Username updateUsername(Username username) {
//        return usernameRepository.save(username);
//    }

    //Удаление пользователя
    public void deleteUsername(Long userId) {
        usernameRepository.findById(userId)                           //находим user-a
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        usernameRepository.deleteById(userId);
    }

    //Удаление связанных с пользователем данных
    public void deleteUsernameLinkedData(Long userId) {
        Username username = usernameRepository.findById(userId)                           //находим user-a
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Удаляем заказы у пользователя
        for (Order order : orderService.getOrdersByUserIdAndArguments(userId, null, null)) {
            orderService.deleteOrderLinkedData(order.getId());
            orderService.deleteOrder(order.getId());
        }
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        // 1. Получаем текущего пользователя из SecurityContext
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        Username user = usernameRepository.findByLogin(login);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден!");
        }

        // 2. Проверяем старый пароль
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Неверный старый пароль!");
        }

        // 3. Проверяем, что новый пароль не совпадает со старым
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Новый пароль должен отличаться от старого!");
        }

        // 4. Хешируем и сохраняем новый пароль
        user.setPassword(passwordEncoder.encode(newPassword));
        usernameRepository.save(user);
    }
}