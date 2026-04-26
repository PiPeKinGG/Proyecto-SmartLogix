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
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        UserVerifyResponse response = new UserVerifyResponse();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash()) && user.getPymeId().equals(request.getPymeId())) {
                response.setValid(true);
                response.setUserId(user.getId());
                response.setPymeId(user.getPymeId());
                response.setRole(user.getRole());
            } else {
                response.setValid(false);
            }
        } else {
            response.setValid(false);
        }
        return response;
    }

    public List<UserDto> getAllUsersByPyme(Long pymeId) {
        return userRepository.findAllByPymeId(pymeId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public UserDto createUser(UserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getEmail())); // Debe venir el password real
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
