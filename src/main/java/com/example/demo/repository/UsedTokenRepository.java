package com.example.demo.repository;
import com.example.demo.Models.UsedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UsedTokenRepository extends JpaRepository<UsedToken, String> {

    // Проверка существования токена в базе
    boolean existsByToken(String token);

    // Удаление токенов с истекшим сроком действия
    @Modifying
    @Query("DELETE FROM UsedToken u WHERE u.expiryDate < :currentDate")
    void deleteExpiredTokens(@Param("currentDate") Date currentDate);
}
