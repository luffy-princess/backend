package com.phishme.backend.repositories;

import com.phishme.backend.entities.PhishingAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhishingAlertRepository extends JpaRepository<PhishingAlert, Long> {
}
