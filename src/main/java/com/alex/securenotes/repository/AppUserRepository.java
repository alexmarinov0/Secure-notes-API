package com.alex.securenotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alex.securenotes.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}