package com.oceanodosdados.repository;

import com.oceanodosdados.domain.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;



@DataJpaTest
class UserRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @DisplayName("Shoud find user by email sucessfully from db")
    @Test
    void findByEmailSucess() {
        User user = createUser();
        this.entityManager.persist(user);

        Optional <User> faundedUser = this.userRepository.findByEmail("test@gmail.com");

        assertTrue(faundedUser.isPresent());
        assertEquals(faundedUser.get().getEmail(), user.getEmail());
    }


    @DisplayName("Shoud not find from db a user by email that does not exist")
    @Test
    void findByEmailFail() {
        User user = createUser();
        Optional <User> faundedUser = this.userRepository.findByEmail("test@gmail.com");
        assertTrue(faundedUser.isEmpty());
    }


    private User createUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@gmail.com");
        user.setPassword("password123");
        return user;
    }

}