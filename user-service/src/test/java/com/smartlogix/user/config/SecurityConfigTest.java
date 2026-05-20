package com.smartlogix.user.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {
    @Test
    void testSecurityConfigLoads() {
        SecurityConfig config = new SecurityConfig();
        assertNotNull(config);
    }
}
