package com.smartlogix.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    @Test
    void testUserDtoSettersAndGetters() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("test@test.com");
        dto.setNombre("Test User");
        dto.setRole("USER");
        dto.setPymeId(100L);
        dto.setIsActive(true);
        dto.setPassword("pass");

        assertEquals(1L, dto.getId());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals("Test User", dto.getNombre());
        assertEquals("USER", dto.getRole());
        assertEquals(100L, dto.getPymeId());
        assertTrue(dto.getIsActive());
        assertEquals("pass", dto.getPassword());
    }
}
