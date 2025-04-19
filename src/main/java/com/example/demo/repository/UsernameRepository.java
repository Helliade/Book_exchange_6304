package com.example.demo.repository;

import com.example.demo.Models.Username;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsernameRepository extends JpaRepository<Username, Long> {
    //  findAll() и findById() определены автоматически
    Username findByLogin(String login);
}