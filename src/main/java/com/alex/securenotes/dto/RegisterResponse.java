package com.alex.securenotes.dto;

public record RegisterResponse(
        String message,
        Long id,
        String username,
        String email
) {
}