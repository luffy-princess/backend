package com.phishme.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phishme.backend.entities.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
    public Optional<Users> findByEmail(String email);

    public Optional<Users> findByNickname(String nickname);
}
