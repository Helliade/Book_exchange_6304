//package com.example.demo;
//
//import com.example.demo.Models.Username;
//import com.example.demo.Repositories.UsernameRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//class UsernameRepositoryTest {
//
//    @Autowired
//    private UsernameRepository usernameRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Test
//    void testFindByLogin_Success() {
//        // given
//        Username user = new Username("test_user", "password");
//        entityManager.persist(user);
//        entityManager.flush();
//
//        // when
//        Username foundUser = usernameRepository.findByLogin("test_user");
//
//        // then
//        assertThat(foundUser).isNotNull();
//        assertThat(foundUser.getLogin()).isEqualTo("test_user");
//    }
//
//    @Test
//    void testFindByLogin_NotFound() {
//        // when
//        Username foundUser = usernameRepository.findByLogin("nonexistent");
//
//        // then
//        assertThat(foundUser).isNull();
//    }
//
//    @Test
//    void testSaveUser() {
//        // given
//        Username user = new Username("new_user", "password");
//
//        // when
//        Username savedUser = usernameRepository.save(user);
//
//        // then
//        assertThat(savedUser).isNotNull();
//        assertThat(savedUser.getId()).isNotNull();
//        assertThat(savedUser.getLogin()).isEqualTo("new_user");
//    }
//}