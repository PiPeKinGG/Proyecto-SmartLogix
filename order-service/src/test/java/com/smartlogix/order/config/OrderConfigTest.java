package com.smartlogix.order.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

class OrderConfigTest {
    @Test
    void testSecurityConfigLoads() {
        SecurityConfig config = new SecurityConfig();
        BCryptPasswordEncoder encoder = config.passwordEncoder();
        
        assertNotNull(encoder);
    }
}
