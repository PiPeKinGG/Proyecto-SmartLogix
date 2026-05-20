package com.smartlogix.user.repository;

import com.smartlogix.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByEmail() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPasswordHash("hash");
        user.setNombre("Test User");
        user.setRole("USER");
        user.setPymeId(100L);
        user.setIsActive(true);
        userRepository.save(user);
        User found = userRepository.findByEmail("test@test.com").orElse(null);
        assertNotNull(found);
        assertEquals("test@test.com", found.getEmail());
    }

    @Test
    void testFindAllByPymeId() {
        User user = new User();
        user.setEmail("test2@test.com");
        user.setPymeId(200L);
        user.setPasswordHash("hash2");
        user.setNombre("Test User 2");
        user.setRole("USER");
        user.setIsActive(true);
        userRepository.save(user);
        List<User> users = userRepository.findAllByPymeId(200L);
        assertFalse(users.isEmpty());
        assertEquals("test2@test.com", users.get(0).getEmail());
    }
}
