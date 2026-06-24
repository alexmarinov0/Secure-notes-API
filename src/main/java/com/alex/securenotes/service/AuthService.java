package com.alex.securenotes.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alex.securenotes.dto.LoginRequest;
import com.alex.securenotes.dto.RegisterRequest;
import com.alex.securenotes.model.AppUser;
import com.alex.securenotes.repository.AppUserRepository;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public AppUser register(RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        AppUser newUser = new AppUser(
                request.getUsername(),
                request.getEmail(),
                hashedPassword
        );

        return userRepository.save(newUser);
    }

    public Optional<LoginResult> login(LoginRequest request) {
        Optional<AppUser> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        AppUser user = optionalUser.get();

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            return Optional.empty();
        }

        String token = jwtService.generateToken(user);

        LoginResult result = new LoginResult(user, token);

        return Optional.of(result);
    }

    public record LoginResult(AppUser user, String token) {
    }
}