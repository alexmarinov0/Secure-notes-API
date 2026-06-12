package com.alex.securenotes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.alex.securenotes.dto.NoteRequest;
import com.alex.securenotes.model.AppUser;
import com.alex.securenotes.model.Note;
import com.alex.securenotes.repository.AppUserRepository;
import com.alex.securenotes.repository.NoteRepository;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final AppUserRepository userRepository;

    public NoteService(NoteRepository noteRepository, AppUserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public Optional<Note> createNote(String username, NoteRequest request) {
        Optional<AppUser> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        AppUser owner = optionalUser.get();

        Note note = new Note(
                request.getTitle(),
                request.getContent(),
                owner
        );

        Note savedNote = noteRepository.save(note);

        return Optional.of(savedNote);
    }

    public List<Note> getNotes(String username) {
        return noteRepository.findByOwnerUsername(username);
    }

    public Optional<Note> getNoteById(Long id, String username) {
        return noteRepository.findByIdAndOwnerUsername(id, username);
    }

    public Optional<Note> updateNote(Long id, String username, NoteRequest request) {
        Optional<Note> optionalNote = noteRepository.findByIdAndOwnerUsername(id, username);

        if (optionalNote.isEmpty()) {
            return Optional.empty();
        }

        Note note = optionalNote.get();

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        Note savedNote = noteRepository.save(note);

        return Optional.of(savedNote);
    }

    public boolean deleteNote(Long id, String username) {
        Optional<Note> optionalNote = noteRepository.findByIdAndOwnerUsername(id, username);

        if (optionalNote.isEmpty()) {
            return false;
        }

        noteRepository.delete(optionalNote.get());

        return true;
    }
}