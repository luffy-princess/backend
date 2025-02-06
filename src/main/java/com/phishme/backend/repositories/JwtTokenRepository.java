package com.phishme.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phishme.backend.entities.JwtTokens;

public interface JwtTokenRepository extends JpaRepository<JwtTokens, Long> {
    public Optional<JwtTokens> findByUserId(Long userId);

    public void deleteByUserId(Long userId);
}
