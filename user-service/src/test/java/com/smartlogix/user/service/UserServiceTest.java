package com.smartlogix.user.service;

import com.smartlogix.user.dto.UserDto;
import com.smartlogix.user.dto.UserVerifyRequest;
import com.smartlogix.user.dto.UserVerifyResponse;
import com.smartlogix.user.entity.User;
import com.smartlogix.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPasswordHash("hashedPassword");
        user.setNombre("Test User");
        user.setRole("USER");
        user.setPymeId(100L);
        user.setIsActive(true);

        userDto = new UserDto();
        userDto.setEmail("test@test.com");
        userDto.setPassword("plainPassword");
        userDto.setNombre("Test User");
        userDto.setRole("USER");
        userDto.setPymeId(100L);
    }

    @Test
    void testCreateUser_Success() {
        // Given
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // When
        UserDto createdUser = userService.createUser(userDto);

        // Then
        assertNotNull(createdUser);
        assertEquals(1L, createdUser.getId());
        assertEquals("test@test.com", createdUser.getEmail());
        assertEquals("Test User", createdUser.getNombre());
        assertEquals("USER", createdUser.getRole());
        assertEquals(100L, createdUser.getPymeId());
        assertTrue(createdUser.getIsActive());

        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testVerifyUser_Success() {
        // Given
        UserVerifyRequest request = new UserVerifyRequest();
        request.setEmail("test@test.com");
        request.setPassword("plainPassword");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "hashedPassword")).thenReturn(true);

        // When
        UserVerifyResponse response = userService.verifyUser(request);

        // Then
        assertNotNull(response);
        assertTrue(response.isValid());
        assertEquals(1L, response.getUserId());
        assertEquals(100L, response.getPymeId());
        assertEquals("USER", response.getRole());

        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(passwordEncoder, times(1)).matches("plainPassword", "hashedPassword");
    }

    @Test
    void testVerifyUser_InvalidPassword() {
        // Given
        UserVerifyRequest request = new UserVerifyRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrongPassword");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // When
        UserVerifyResponse response = userService.verifyUser(request);

        // Then
        assertNotNull(response);
        assertFalse(response.isValid());
        assertNull(response.getUserId());
        assertNull(response.getPymeId());
        assertNull(response.getRole());

        verify(userRepository, times(1)).findByEmail("test@test.com");
        verify(passwordEncoder, times(1)).matches("wrongPassword", "hashedPassword");
    }

    @Test
    void testGetUsersByPymeId() {
        // Given
        Long pymeId = 100L;
        when(userRepository.findAllByPymeId(pymeId)).thenReturn(List.of(user));

        // When
        List<UserDto> users = userService.getAllUsersByPyme(pymeId);

        // Then
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(pymeId, users.get(0).getPymeId());
        assertEquals("test@test.com", users.get(0).getEmail());

        verify(userRepository, times(1)).findAllByPymeId(pymeId);
    }
}