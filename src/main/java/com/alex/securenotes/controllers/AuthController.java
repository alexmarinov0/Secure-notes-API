package com.alex.securenotes.controllers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alex.securenotes.dto.LoginRequest;
import com.alex.securenotes.dto.RegisterRequest;
import com.alex.securenotes.model.AppUser;
import com.alex.securenotes.service.AuthService;
import com.alex.securenotes.service.AuthService.LoginResult;

import jakarta.validation.Valid;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        if (authService.usernameExists(request.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already taken"));
        }

        if (authService.emailExists(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already taken"));
        }

        AppUser savedUser = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "id", savedUser.getId(),
                        "username", savedUser.getUsername(),
                        "email", savedUser.getEmail()
                ));
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        Optional<LoginResult> optionalLoginResult = authService.login(request);

        if (optionalLoginResult.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }

        LoginResult loginResult = optionalLoginResult.get();
        AppUser user = loginResult.user();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Login successful",
                        "token", loginResult.token(),
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail()
                ));
    }

    private ResponseEntity<?> validationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }
}