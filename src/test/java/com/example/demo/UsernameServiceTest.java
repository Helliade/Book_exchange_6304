//package com.example.demo;
//
//
//import com.example.demo.Models.Username;
//import com.example.demo.Repositories.UsernameRepository;
//import com.example.demo.services.UsernameService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.test.context.TestPropertySource;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@SpringBootTest
//@TestPropertySource(properties = {
//        "spring.flyway.enabled=false",
//        "spring.jpa.hibernate.ddl-auto=create-drop",
//        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
//})
//class UsernameServiceTest {
//
//    @Autowired
//    private UsernameService usernameService;
//
//    @Autowired
//    private UsernameRepository usernameRepository;
//
//    @AfterEach
//    void tearDown() {
//        // Очистка базы данных после каждого теста
//        usernameRepository.deleteAll();
//    }
//
//    @Test
//    void testCreateUser_Success() {
//        // when
//        Username createdUser = usernameService.registerUser("new_user", "password");
//
//        // then
//        assertThat(createdUser).isNotNull();
//        assertThat(createdUser.getId()).isNotNull();
//        assertThat(createdUser.getLogin()).isEqualTo("new_user");
//        assertThat(createdUser.getPassword()).isEqualTo("password");
//    }
//
//    @Test
//    void testCreateUser_LoginAlreadyExists() {
//        // given
//        usernameService.registerUser("existing_user", "password");
//
//        // when & then
//        assertThatThrownBy(() -> usernameService.registerUser("existing_user", "password"))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("Login already exists");
//    }
//
//    @Test
//    void testGetUserByLogin_Success() {
//        // given
//        Username user = new Username("existing_user", "password");
//        usernameRepository.save(user);
//
//        // when
//        Username foundUser = usernameService.getUserByLogin("existing_user");
//
//        // then
//        assertThat(foundUser).isNotNull();
//        assertThat(foundUser.getLogin()).isEqualTo("existing_user");
//    }
//
//    @Test
//    void testGetUserByLogin_NotFound() {
//        // when & then
//        assertThatThrownBy(() -> usernameService.getUserByLogin("nonexistent"))
//                .isInstanceOf(UsernameNotFoundException.class)
//                .hasMessage("User not found with login: nonexistent");
//    }
//}