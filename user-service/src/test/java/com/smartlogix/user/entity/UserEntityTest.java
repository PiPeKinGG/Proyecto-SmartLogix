package com.smartlogix.user.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    @Test
    void testUserEntitySettersAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPasswordHash("hash");
        user.setNombre("Test User");
        user.setRole("USER");
        user.setPymeId(100L);
        user.setIsActive(true);

        assertEquals(1L, user.getId());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("hash", user.getPasswordHash());
        assertEquals("Test User", user.getNombre());
        assertEquals("USER", user.getRole());
        assertEquals(100L, user.getPymeId());
        assertTrue(user.getIsActive());
    }
}
