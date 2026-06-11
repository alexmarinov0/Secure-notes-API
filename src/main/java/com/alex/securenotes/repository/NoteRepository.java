package com.alex.securenotes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alex.securenotes.model.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByOwnerUsername(String username);

    Optional<Note> findByIdAndOwnerUsername(Long id, String username);
}