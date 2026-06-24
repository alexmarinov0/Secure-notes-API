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

import com.alex.securenotes.dto.ErrorResponse;
import com.alex.securenotes.dto.LoginRequest;
import com.alex.securenotes.dto.LoginResponse;
import com.alex.securenotes.dto.RegisterRequest;
import com.alex.securenotes.dto.RegisterResponse;
import com.alex.securenotes.dto.ValidationErrorResponse;
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
                    .body(new ErrorResponse("Username already taken"));
        }

        if (authService.emailExists(request.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Email already taken"));
        }

        AppUser savedUser = authService.register(request);

        RegisterResponse response = new RegisterResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
                    .body(new ErrorResponse("Invalid username or password"));
        }

        LoginResult loginResult = optionalLoginResult.get();
        AppUser user = loginResult.user();

        LoginResponse response = new LoginResponse(
                "Login successful",
                loginResult.token(),
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    private ResponseEntity<?> validationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(errors));
    }
}