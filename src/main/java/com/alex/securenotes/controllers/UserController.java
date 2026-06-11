package com.alex.securenotes.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alex.securenotes.model.AppUser;
import com.alex.securenotes.repository.AppUserRepository;

@RestController
public class UserController {

    private final AppUserRepository userRepository;

    public UserController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

@GetMapping("/api/users/me")
public ResponseEntity<?> getCurrentUser(Authentication authentication) {
    String username = authentication.getName();

    Optional<AppUser> optionalUser = userRepository.findByUsername(username);

    if (optionalUser.isEmpty()) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found"));
    }

    AppUser user = optionalUser.get();

    return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail()
    ));
}
}
