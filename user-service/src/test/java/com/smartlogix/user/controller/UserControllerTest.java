package com.smartlogix.user.controller;

import com.smartlogix.user.dto.UserDto;
import com.smartlogix.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testGetAllUsersByPyme() {
        Long pymeId = 100L;
        UserDto userDto = new UserDto();
        userDto.setPymeId(pymeId);
        userDto.setEmail("test@test.com");
        when(userService.getAllUsersByPyme(pymeId)).thenReturn(List.of(userDto));

        List<UserDto> response = userController.getAll(pymeId);
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("test@test.com", response.get(0).getEmail());
    }
}
