package com.phishme.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phishme.backend.entities.Terms;

public interface TermRepository extends JpaRepository<Terms, Long> {
    List<Terms> findByForRegistrationAndDisabled(Boolean for_registration, Boolean disabled);

    List<Terms> findByDisabled(Boolean disabled);

    Optional<Terms> findByIdAndDisabled(Long id, Boolean disabled);
}
