package com.example.demo.repository;

import com.example.demo.Models.Work;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {
    //  findAll() и findById() определены автоматически
    List<Work> findByGenre(String genre);
    List<Work> findByName(String name);
    List<Work> findByWriter(String writer);
    List<Work> findByYear(Long year);
}