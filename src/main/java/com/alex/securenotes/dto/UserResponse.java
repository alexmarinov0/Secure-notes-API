package com.alex.securenotes.dto;

public record UserResponse(
        Long id,
        String username,
        String email
) {
}