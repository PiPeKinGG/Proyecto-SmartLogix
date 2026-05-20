package com.smartlogix.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {
    @Test
    void testSettersAndGetters() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("pass");
        assertEquals("test@test.com", req.getEmail());
        assertEquals("pass", req.getPassword());
    }
}
