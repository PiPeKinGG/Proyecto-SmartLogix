package com.smartlogix.user.dto;

public class UserDto {
    private Long id;
    private String email;
    private String nombre;
    private String role;
    private Long pymeId;
    private String password;
    
    // Nuevo campo agregado al DTO
    private Boolean isActive;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Long getPymeId() { return pymeId; }
    public void setPymeId(Long pymeId) { this.pymeId = pymeId; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Getters y setters para isActive
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}