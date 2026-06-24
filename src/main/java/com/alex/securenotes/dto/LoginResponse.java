package com.alex.securenotes.dto;

public record LoginResponse(
        String message,
        String token,
        Long id,
        String username,
        String email
) {
}