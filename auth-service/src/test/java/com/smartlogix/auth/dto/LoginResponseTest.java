package com.smartlogix.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {
    @Test
    void testSettersAndGetters() {
        LoginResponse res = new LoginResponse("token", 1L, 100L, "USER");
        assertEquals("token", res.getToken());
        assertEquals(1L, res.getUserId());
        assertEquals(100L, res.getPymeId());
        assertEquals("USER", res.getRole());
        
        res.setToken("token2");
        res.setUserId(2L);
        res.setPymeId(200L);
        res.setRole("ADMIN");
        assertEquals("token2", res.getToken());
        assertEquals(2L, res.getUserId());
        assertEquals(200L, res.getPymeId());
        assertEquals("ADMIN", res.getRole());
    }
}
