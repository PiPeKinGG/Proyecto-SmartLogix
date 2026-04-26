package com.smartlogix.user.controller;

import com.smartlogix.user.dto.UserDto;
import com.smartlogix.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}
