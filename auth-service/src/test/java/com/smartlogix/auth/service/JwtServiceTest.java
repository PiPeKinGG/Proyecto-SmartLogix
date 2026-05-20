package com.smartlogix.auth.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Test
    void testGenerateToken() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        String token = jwtService.generateToken(1L, 100L, "USER");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
}
