package com.alex.securenotes.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alex.securenotes.dto.NoteRequest;
import com.alex.securenotes.model.Note;
import com.alex.securenotes.service.NoteService;

import jakarta.validation.Valid;

@RestController
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/api/notes")
    public ResponseEntity<?> createNote(
            @Valid @RequestBody NoteRequest request,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        String username = authentication.getName();

        Optional<Note> optionalNote = noteService.createNote(username, request);

        if (optionalNote.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(noteResponse(optionalNote.get()));
    }

    @GetMapping("/api/notes")
    public ResponseEntity<?> getNotes(Authentication authentication) {
        String username = authentication.getName();

        List<Note> notes = noteService.getNotes(username);

        List<Map<String, Object>> response = notes.stream()
                .map(this::noteResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/notes/{id}")
    public ResponseEntity<?> getNoteById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();

        Optional<Note> optionalNote = noteService.getNoteById(id, username);

        if (optionalNote.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Note not found"));
        }

        return ResponseEntity.ok(noteResponse(optionalNote.get()));
    }

    @PutMapping("/api/notes/{id}")
    public ResponseEntity<?> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest request,
            BindingResult bindingResult,
            Authentication authentication
    ) {
        if (bindingResult.hasErrors()) {
            return validationErrorResponse(bindingResult);
        }

        String username = authentication.getName();

        Optional<Note> optionalNote = noteService.updateNote(id, username, request);

        if (optionalNote.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Note not found"));
        }

        return ResponseEntity.ok(noteResponse(optionalNote.get()));
    }

    @DeleteMapping("/api/notes/{id}")
    public ResponseEntity<?> deleteNote(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();

        boolean deleted = noteService.deleteNote(id, username);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Note not found"));
        }

        return ResponseEntity.ok(Map.of("message", "Note deleted successfully"));
    }

    private Map<String, Object> noteResponse(Note note) {
        return Map.of(
                "id", note.getId(),
                "title", note.getTitle(),
                "content", note.getContent()
        );
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