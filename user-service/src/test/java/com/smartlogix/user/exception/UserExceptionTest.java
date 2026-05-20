package com.smartlogix.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserExceptionTest {
    @Test
    void testUserExceptionMessage() {
        UserException ex = new UserException("User not found");
        assertEquals("User not found", ex.getMessage());
    }
}
