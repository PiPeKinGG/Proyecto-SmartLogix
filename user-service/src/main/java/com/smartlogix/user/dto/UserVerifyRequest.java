package com.smartlogix.user.dto;

public class UserVerifyRequest {
    private String email;
    private String password;
    private Long pymeId;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Long getPymeId() { return pymeId; }
    public void setPymeId(Long pymeId) { this.pymeId = pymeId; }
}
