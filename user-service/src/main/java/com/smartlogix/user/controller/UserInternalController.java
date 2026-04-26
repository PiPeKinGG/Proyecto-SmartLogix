package com.smartlogix.user.controller;

import com.smartlogix.user.dto.UserVerifyRequest;
import com.smartlogix.user.dto.UserVerifyResponse;
import com.smartlogix.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
public class UserInternalController {
    @Autowired
    private UserService userService;

    @PostMapping("/verify")
    public UserVerifyResponse verify(@RequestBody UserVerifyRequest request) {
        return userService.verifyUser(request);
    }
}
