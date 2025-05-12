package com.example.demo.repository;

import com.example.demo.Models.Book;
import com.example.demo.Models.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BookRepository extends JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book> {
    //  findAll() и findById() определены автоматически
    List<Book> findAll();
//    List<Book> findByStatus(String status);
//    List<Book> findByCondit(String condit);
//    List<Book> findByYearOfPubl(Short yearOfPubl);
//    List<Book> findByLanguage(String language);
//    List<Book> findByPublHouse(String publHouse);

    @Query(value = """
        SELECT bc.Creation_id
        FROM Book_Creation bc
        WHERE bc.Book_id = :bookId
        """, nativeQuery = true)
    List<Long> findCreationIdsByBookId(@Param("bookId") Long bookId);

}

