package com.alex.securenotes.controllers;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alex.securenotes.dto.RegisterRequest;
import com.alex.securenotes.model.AppUser;
import com.alex.securenotes.repository.AppUserRepository;

@RestController
public class AuthController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/api/auth/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        AppUser newUser = new AppUser(request.getUsername(), request.getEmail(), hashedPassword);

        AppUser savedUser = userRepository.save(newUser);

        return Map.of(
            "message", "User registered successfully",
            "id", savedUser.getId(),
            "username", savedUser.getUsername(),
            "email", savedUser.getEmail()
        );
    }
}