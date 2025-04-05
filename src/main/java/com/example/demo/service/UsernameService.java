package com.example.demo.service;

import com.example.demo.Models.Username;
import com.example.demo.repository.UsernameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;


@Service
public class UsernameService {

    private final UsernameRepository usernameRepository;
    private final BCryptPasswordEncoder passwordEncoder;
//
//    public UsernameService(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    // Хэширование пароля перед сохранением в БД
//    public String encodePassword(String rawPassword) {
//        return passwordEncoder.encode(rawPassword);
//    }
//
//    // Проверка пароля (например, при логине)
//    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
//        return passwordEncoder.matches(rawPassword, encodedPassword);
//    }

    @Autowired
    public UsernameService(UsernameRepository usernameRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usernameRepository = usernameRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Получение списка всех пользователей
    public List<Username> getAllUsernames() {
        return usernameRepository.findAll();
    }

    //Получение пользователя по ID
    public Username getUsernameById(Long id) {
        return usernameRepository.findById(id).orElse(null);
    }

    //Создание пользователя
//    public Username createUsername(Username username) {
//        return usernameRepository.save(username);
//    }

    @Transactional
    public Username registerUsername(Username username) {
        // Проверка существования пользователя
        if (usernameRepository.findByLogin(username.getLogin()) != null) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }

        // Создание нового пользователя
        username.setPassword(passwordEncoder.encode(username.getPassword())); // Хеширование пароля
        return usernameRepository.save(username);
    }

    public boolean authenticateUsername(Username username) { //String login, String rawPassword) {
        // Поиск пользователя по логину
        Username user = usernameRepository.findByLogin(username.getLogin());
        if (user == null) {
            return false; // Пользователь не найден
        }

        // Проверка пароля
        return passwordEncoder.matches(username.getPassword(), user.getPassword()); //метод проверяет, соответсвует ли пароль
    }                                                                               //пользователя хешу, хранящимуся в БД

    //Обновление пользователя
    public Username updateUsername(Username username) {
        return usernameRepository.save(username);
    }

    //Удаление пользователя
    public void deleteUsername(Long id) {
        usernameRepository.deleteById(id);
    }
}
//import com.example.demo.Models.Username;
//import com.example.demo.repository.UsernameRepository;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Service
//public class UsernameService {
//
//    private final UsernameRepository usernameRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Autowired // Внедрение зависимостей через конструктор
//    public UsernameService(UsernameRepository usernameRepository) {
//        this.usernameRepository = usernameRepository;
//        this.passwordEncoder = new BCryptPasswordEncoder();
//    }
//
//    @Transactional
//    public Username registerUser(String login, String rawPassword) {
//        // Проверка существования пользователя
//        if (usernameRepository.findByLogin(login) != null) {
//            throw new RuntimeException("Пользователь с таким логином уже существует");
//        }
//
//        // Создание нового пользователя
//        Username username = new Username();
//        username.setLogin(login);
//        username.setPassword(passwordEncoder.encode(rawPassword)); // Хеширование пароля
//        return usernameRepository.save(username);
//    }
//
//
//    public boolean authenticate(String login, String rawPassword) {
//        // Поиск пользователя по логину
//        Username username = usernameRepository.findByLogin(login);
//        if (username == null) {
//            return false; // Пользователь не найден
//        }
//
//        // Проверка пароля
//        return passwordEncoder.matches(rawPassword, username.getPassword());
//    }
//
//    // Дополнительный метод для смены пароля
//    @Transactional
//    public void changePassword(String login, String oldPassword, String newPassword) {
//        // Аутентификация перед сменой пароля
//        if (!authenticate(login, oldPassword)) {
//            throw new RuntimeException("Неверный текущий пароль");
//        }
//
//        Username username = usernameRepository.findByLogin(login);
//        username.setPassword(passwordEncoder.encode(newPassword));
//        usernameRepository.save(username);
//    }
//
//    public Username getUsernameById(Long id) {
//        return usernameRepository.findById(id)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }
//
//    public Username getUserByLogin(String login) {
//        Username user = usernameRepository.findByLogin(login);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with login: " + login);
//        }
//        return user;
//    }
//
//}

//Ключевые изменения:
//  Добавила проверку существования пользователя при регистрации.
//  Создала метод смены пароля с дополнительной проверкой.
//  Использовала BCryptPasswordEncoder для безопасного хеширования паролей.
//  Создала репозиторий с методом поиска по логину.