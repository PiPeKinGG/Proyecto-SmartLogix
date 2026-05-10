package com.smartlogix.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.smartlogix.auth.dto.LoginRequest;
import com.smartlogix.auth.dto.UserVerificationResponse;

@FeignClient(name = "user-service")
public interface UserClient {
    @PostMapping("/internal/users/verify")
    UserVerificationResponse verifyUser(@RequestBody LoginRequest request);
}