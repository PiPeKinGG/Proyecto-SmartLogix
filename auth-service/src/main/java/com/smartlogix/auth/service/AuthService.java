package com.smartlogix.auth.service;

import com.smartlogix.auth.dto.LoginRequest;
import com.smartlogix.auth.dto.LoginResponse;
import com.smartlogix.auth.dto.UserVerificationResponse;
import com.smartlogix.auth.feign.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        UserVerificationResponse verification = userClient.verifyUser(request);
        if (verification != null && verification.isValid()) {
            String token = jwtService.generateToken(
                    verification.getUserId(),
                    verification.getPymeId(),
                    verification.getRole());
            return new LoginResponse(token, verification.getUserId(), verification.getPymeId(), verification.getRole());
        } else {
            throw new RuntimeException("Credenciales inválidas");
        }
    }
}