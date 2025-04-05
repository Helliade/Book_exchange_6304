package com.example.demo.repository;

import com.example.demo.Models.Username;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsernameRepository extends JpaRepository<Username, Long> {
    // Метод для поиска пользователя по логину
    Username findByLogin(String login);
//    Optional<Username> findByLogin(String login);
    // Метод для поиска пользователя по ID
}
