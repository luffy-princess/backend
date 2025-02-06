package com.phishme.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phishme.backend.dto.TermResponse;
import com.phishme.backend.service.TermService;

@RestController
public class TermController {

    @Autowired
    private TermService termService;

    @GetMapping("/terms")
    public TermResponse getTermsController() {
        return new TermResponse(termService.findAvailableTerms());
    }

    @GetMapping("/terms/registration")
    public TermResponse getRegistrationTermsController() {
        return new TermResponse(termService.findRegistrationTerms());
    }
}
