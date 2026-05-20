package com.smartlogix.auth.controller;

import com.smartlogix.auth.dto.LoginRequest;
import com.smartlogix.auth.dto.LoginResponse;
import com.smartlogix.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@smartlogix.com");
        loginRequest.setPassword("secret123");

        loginResponse = new LoginResponse("mocked-token", 1L, 100L, "ADMIN");
    }

    @Test
    void testLogin_Success() {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mocked-token", response.getBody().getToken());
        verify(authService, times(1)).login(any(LoginRequest.class));
    }
}