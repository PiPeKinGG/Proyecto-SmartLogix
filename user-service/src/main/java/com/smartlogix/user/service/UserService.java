package com.smartlogix.user.service;

import com.smartlogix.user.dto.UserDto;
import com.smartlogix.user.dto.UserVerifyRequest;
import com.smartlogix.user.dto.UserVerifyResponse;
import com.smartlogix.user.entity.User;
import com.smartlogix.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UserVerifyResponse verifyUser(UserVerifyRequest request) {
        System.out.println(">>> [DEBUG] INICIANDO LOGIN PARA EMAIL: '" + request.getEmail() + "'");
        System.out.println(">>> [DEBUG] PASSWORD RECIBIDO: '" + request.getPassword() + "'");
        
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        UserVerifyResponse response = new UserVerifyResponse();
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println(">>> [DEBUG] USUARIO ENCONTRADO. Hash en BD: " + user.getPasswordHash());
            
            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
            System.out.println(">>> [DEBUG] ¿COINCIDEN LAS CONTRASEÑAS?: " + matches);
            
            if (matches) {
                response.setValid(true);
                response.setUserId(user.getId());
                response.setPymeId(user.getPymeId());
                response.setRole(user.getRole());
                System.out.println(">>> [DEBUG] LOGIN EXITOSO");
            } else {
                response.setValid(false);
                System.out.println(">>> [DEBUG] FALLO: CONTRASEÑA INCORRECTA");
            }
        } else {
            response.setValid(false);
            System.out.println(">>> [DEBUG] FALLO: USUARIO NO EXISTE EN LA BASE DE DATOS");
        }
        return response;
    }

    public List<UserDto> getAllUsersByPyme(Long pymeId) {
        return userRepository.findAllByPymeId(pymeId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDto createUser(UserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // Se encripta la contraseña correctamente
        user.setNombre(dto.getNombre());
        user.setRole(dto.getRole());
        user.setPymeId(dto.getPymeId());
        user = userRepository.save(user);
        return toDto(user);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNombre(user.getNombre());
        dto.setRole(user.getRole());
        dto.setPymeId(user.getPymeId());
        return dto;
    }
}