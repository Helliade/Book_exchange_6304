package com.example.demo.service;

import com.example.demo.DTO.UsernameDTO;
import com.example.demo.Models.Username;
import com.example.demo.repository.UsernameRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public UsernameDTO getUsernameById(Long id) {
        Username username = usernameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Username not found"));

        return convertToDTO(username);
    }

    private UsernameDTO convertToDTO(Username username) {
        return new UsernameDTO(
                username.getId(),
                username.getLogin()
        );
    }



    @Transactional
    public UsernameDTO registerUsername(Username username) {
        // Проверка существования пользователя
        if (usernameRepository.findByLogin(username.getLogin()) != null) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }

        // Создание нового пользователя
        username.setPassword(username.getPassword());                 //passwordEncoder.encode(username.getPassword())); // Хеширование пароля
        return convertToDTO(usernameRepository.save(username));
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


    public Username getUserByLogin(String login) {
        Username user = usernameRepository.findByLogin(login);
        if (user == null) {
            return null;
//            throw new UsernameNotFoundException("User not found with login: " + login);
        }
        return user;
    }

    //TODO Обновление пользователя   !!!!
    public Username updateUsername(Username username) {
        return usernameRepository.save(username);
    }

    //Удаление пользователя
    public void deleteUsername(Long id) {
        usernameRepository.deleteById(id);
    }
}
//
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

//Ключевые изменения:
//  Добавила проверку существования пользователя при регистрации.
//  Создала метод смены пароля с дополнительной проверкой.
//  Использовала BCryptPasswordEncoder для безопасного хеширования паролей.
//  Создала репозиторий с методом поиска по логину.