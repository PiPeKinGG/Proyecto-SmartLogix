package com.smartlogix.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Long pymeId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String role;

    // Nuevo campo para saber si el usuario está habilitado
    @Column(name = "is_active", columnDefinition = "boolean default true")
    private Boolean isActive;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public Long getPymeId() { return pymeId; }
    public void setPymeId(Long pymeId) { this.pymeId = pymeId; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Getters y setters para isActive
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}