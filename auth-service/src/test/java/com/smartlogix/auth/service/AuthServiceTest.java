package com.smartlogix.auth.service;

import com.smartlogix.auth.dto.LoginRequest;
import com.smartlogix.auth.dto.LoginResponse;
import com.smartlogix.auth.dto.UserVerificationResponse;
import com.smartlogix.auth.feign.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserClient userClient;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private UserVerificationResponse verificationResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");

        verificationResponse = new UserVerificationResponse();
        verificationResponse.setValid(true);
        verificationResponse.setUserId(1L);
        verificationResponse.setPymeId(100L);
        verificationResponse.setRole("ADMIN");
    }

    @Test
    void testLogin_Success() {
        // Given
        when(userClient.verifyUser(any(LoginRequest.class))).thenReturn(verificationResponse);
        when(jwtService.generateToken(anyLong(), anyLong(), anyString())).thenReturn("mocked-jwt-token");

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals(100L, response.getPymeId());
        assertEquals("ADMIN", response.getRole());

        verify(userClient, times(1)).verifyUser(any(LoginRequest.class));
        verify(jwtService, times(1)).generateToken(1L, 100L, "ADMIN");
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Given
        verificationResponse.setValid(false);
        when(userClient.verifyUser(any(LoginRequest.class))).thenReturn(verificationResponse);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(userClient, times(1)).verifyUser(any(LoginRequest.class));
        verify(jwtService, never()).generateToken(anyLong(), anyLong(), anyString());
    }

    @Test
    void testLogin_UserNotFound() {
        // Given
        when(userClient.verifyUser(any(LoginRequest.class))).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(userClient, times(1)).verifyUser(any(LoginRequest.class));
        verify(jwtService, never()).generateToken(anyLong(), anyLong(), anyString());
    }
}