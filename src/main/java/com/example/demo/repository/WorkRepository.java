package com.example.demo.repository;

import com.example.demo.Models.Book;
import com.example.demo.Models.Order;
import com.example.demo.Models.Work;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long>,
        JpaSpecificationExecutor<Work> {
    //  findAll() и findById() определены автоматически
    List<Work> findByGenre(String genre);
    List<Work> findByName(String name);
    List<Work> findByWriter(String writer);
    List<Work> findByYear(Long year);

    @Query(value = "SELECT c.* FROM Creation c " +
            "JOIN Book_Creation bc ON c.id = bc.Creation_id " +
            "WHERE bc.Book_id = :bookId", nativeQuery = true)
    List<Work> findByBookId(@Param("bookId") Long bookId);
}