package com.smartlogix.auth.dto;

public class UserVerificationResponse {
    private boolean valid;
    private Long userId;
    private Long pymeId;
    private String role;

    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
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
