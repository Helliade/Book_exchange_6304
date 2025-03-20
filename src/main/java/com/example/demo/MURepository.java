package com.example.demo;

import com.example.demo.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Явно укажите @Repository

@Repository // Указываем, что это Spring репозиторий
public interface MURepository extends JpaRepository<User, Long> {

    // Вы можете добавлять свои собственные методы запросов здесь, если нужно
    // Например, поиск пользователя по имени
    User findByLogin(String login);

    // Spring Data JPA автоматически реализует эти методы на основе имени
    // findBy<ИмяПоля>
    // findAllBy<ИмяПоля>And<ДругоеИмяПоля> и т.д.
}

//Объяснение:
//
//@Repository: Аннотация Spring, указывающая, что это компонент репозитория. Spring будет управлять жизненным циклом этого бина.
//  UserRepository extends JpaRepository<User, Long>: Интерфейс UserRepository расширяет JpaRepository.
//  User: Тип сущности, с которой будет работать репозиторий.
//  Long: Тип первичного ключа сущности User (в данном случае Long для поля id).
//  findByUsername(String username): Spring Data JPA автоматически реализует этот метод на основе имени метода.
//Он будет генерировать запрос для поиска пользователя по имени пользователя. Вы можете добавлять другие пользовательские
//методы запросов, следуя правилам именования Spring Data JPA.