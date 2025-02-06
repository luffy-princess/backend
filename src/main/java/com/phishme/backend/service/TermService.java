package com.phishme.backend.service;

import java.util.List;

import com.phishme.backend.entities.TermAgreement;
import com.phishme.backend.entities.Terms;

public interface TermService {
    public Terms findById(Long termId);

    public List<Terms> findAvailableTerms();

    public List<Terms> findRegistrationTerms();

    public void saveAllTermArgeements(List<TermAgreement> termAgreements);
}