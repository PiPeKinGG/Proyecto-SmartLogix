package com.smartlogix.user.controller;

import com.smartlogix.user.dto.UserDto;
import com.smartlogix.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDto> getAll(@RequestHeader("pyme_id") Long pymeId) {
        return userService.getAllUsersByPyme(pymeId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto dto, @RequestHeader("pyme_id") Long pymeId) {
        dto.setPymeId(pymeId);
        return userService.createUser(dto);
    }

    // Endpoint para actualizar/editar usuario
    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto, @RequestHeader("pyme_id") Long pymeId) {
        dto.setPymeId(pymeId);
        return userService.updateUser(id, dto); 
    }

    // Endpoint para habilitar/deshabilitar usuario
    @PatchMapping("/{id}/status")
    public void updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> statusMap) {
        Boolean isActive = statusMap.get("isActive");
        userService.updateUserStatus(id, isActive);
    }
}