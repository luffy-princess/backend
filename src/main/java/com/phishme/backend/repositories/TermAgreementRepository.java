package com.phishme.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phishme.backend.entities.TermAgreement;

public interface TermAgreementRepository extends JpaRepository<TermAgreement, Long> {

}
