package com.example.demo.config;

import com.example.demo.Models.UsedToken;
import com.example.demo.repository.UsedTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;

@Service
public class TokenUsageService {
    @Autowired
    private UsedTokenRepository usedTokenRepository;

    // Помечаем токен как использованный
    public void markTokenAsUsed(String token, Date expiryDate) {
        UsedToken usedToken = new UsedToken();
        usedToken.setToken(token);
        usedToken.setExpiryDate(expiryDate);
        usedTokenRepository.save(usedToken);
    }

    // Проверяем, был ли токен уже использован
    public boolean isTokenUsed(String token) {
        return usedTokenRepository.existsById(token);
    }

    // Очистка устаревших токенов (можно вызывать по расписанию)
    @Scheduled(fixedRate = 1800000) // Каждые полчаса
    @Transactional
    public void cleanupExpiredTokens() {
        usedTokenRepository.deleteExpiredTokens(Date.from(Instant.now()));
    }
}
