package com.example.demo.service;

import com.example.demo.Models.Username;
import com.example.demo.repository.UsernameRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;


@Service
public class UsernameService {

    private final UsernameRepository usernameRepository;
//    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UsernameService(UsernameRepository usernameRepository){ //, BCryptPasswordEncoder passwordEncoder) {
        this.usernameRepository = usernameRepository;
//        this.passwordEncoder = passwordEncoder;
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
        Username username = new Username(login, password);
        //passwordEncoder.encode(password); // Хеширование пароля
        return usernameRepository.save(username);
    }


    public boolean authenticateUsername(String login, String rawPassword) { //String login, String rawPassword) {
        // Поиск пользователя по логину
        Username user = usernameRepository.findByLogin(login);
        if (user == null) {
            return false; // Пользователь не найден
        }
        // Проверка пароля
        return rawPassword.equals(user.getPassword());         //passwordEncoder.matches(rawPassword, user.getPassword()); //метод проверяет, соответсвует ли пароль
    }                                                                               //пользователя хешу, хранящимуся в БД

//    //TODO Обновление пользователя   !!!!
//    public Username updateUsername(Username username) {
//        return usernameRepository.save(username);
//    }

    //Удаление пользователя
    public void deleteUsername(Long userId) {

        usernameRepository.findById(userId)                           //находим user-a
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        usernameRepository.deleteById(userId);
    }
}

//@Service
//public class UsernameService {
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
//}