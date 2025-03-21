package com.example.demo.services;


import com.example.demo.Models.Username;
import com.example.demo.Repositories.UsernameRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsernameService {

    private final UsernameRepository usernameRepository;
    private final PasswordEncoder passwordEncoder;

    // Внедрение зависимостей через конструктор
    public UsernameService(UsernameRepository usernameRepository) {
        this.usernameRepository = usernameRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public Username registerUser(String login, String rawPassword) {
        // Проверка существования пользователя
        if (usernameRepository.findByLogin(login) != null) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }

        // Создание нового пользователя
        Username username = new Username();
        username.setLogin(login);
        username.setPassword(passwordEncoder.encode(rawPassword)); // Хеширование пароля
        return usernameRepository.save(username);
    }

    public boolean authenticate(String login, String rawPassword) {
        // Поиск пользователя по логину
        Username username = usernameRepository.findByLogin(login);
        if (username == null) {
            return false; // Пользователь не найден
        }

        // Проверка пароля
        return passwordEncoder.matches(rawPassword, username.getPassword());
    }

    // Дополнительный метод для смены пароля
    @Transactional
    public void changePassword(String login, String oldPassword, String newPassword) {
        // Аутентификация перед сменой пароля
        if (!authenticate(login, oldPassword)) {
            throw new RuntimeException("Неверный текущий пароль");
        }

        Username username = usernameRepository.findByLogin(login);
        username.setPassword(passwordEncoder.encode(newPassword));
        usernameRepository.save(username);
    }
}

//    Ключевые изменения:
//
//        Добавила проверку существования пользователя при регистрации
//        Создала метод смены пароля с дополнительной проверкой
//        Использовала BCryptPasswordEncoder для безопасного хеширования паролей
//        Создала репозиторий с методом поиска по логину