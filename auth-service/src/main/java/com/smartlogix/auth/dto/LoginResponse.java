package com.smartlogix.auth.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private Long pymeId;
    private String role;

    public LoginResponse(String token, Long userId, Long pymeId, String role) {
        this.token = token;
        this.userId = userId;
        this.pymeId = pymeId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getPymeId() {
        return pymeId;
    }
    public void setPymeId(Long pymeId) {
        this.pymeId = pymeId;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
