package com.alex.securenotes.dto;

public record NoteResponse(
        Long id,
        String title,
        String content
) {
}