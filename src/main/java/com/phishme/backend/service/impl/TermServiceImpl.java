package com.phishme.backend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phishme.backend.entities.TermAgreement;
import com.phishme.backend.entities.Terms;
import com.phishme.backend.repositories.TermAgreementRepository;
import com.phishme.backend.repositories.TermRepository;
import com.phishme.backend.service.TermService;

@Service
public class TermServiceImpl implements TermService {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    TermAgreementRepository termAgreementRepository;

    @Override
    public Terms findById(Long termId) {
        return termRepository.findByIdAndDisabled(termId, false).orElse(null);
    }

    @Override
    public List<Terms> findAvailableTerms() {
        return termRepository.findByDisabled(false);
    }

    @Override
    public List<Terms> findRegistrationTerms() {
        return termRepository.findByForRegistrationAndDisabled(true, false);
    }

    @Override
    public void saveAllTermArgeements(List<TermAgreement> termAgreements) {
        termAgreementRepository.saveAll(termAgreements);
    }
}
