package com.smartlogix.auth.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserVerificationResponseTest {
    @Test
    void testSettersAndGetters() {
        UserVerificationResponse res = new UserVerificationResponse();
        res.setValid(true);
        res.setUserId(1L);
        res.setPymeId(100L);
        res.setRole("USER");
        assertTrue(res.isValid());
        assertEquals(1L, res.getUserId());
        assertEquals(100L, res.getPymeId());
        assertEquals("USER", res.getRole());
    }
}
