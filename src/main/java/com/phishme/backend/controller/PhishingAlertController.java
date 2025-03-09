package com.phishme.backend.controller;

import com.phishme.backend.dto.PhishingAlertDto;
import com.phishme.backend.service.PhishingAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phishing-alerts")
@RequiredArgsConstructor
public class PhishingAlertController {
    private final PhishingAlertService phishingAlertService;

    @GetMapping
    public ResponseEntity<List<PhishingAlertDto>> getCurrentPhishingAlerts() {
        return ResponseEntity.ok(phishingAlertService.getCurrentPhishingAlerts());
    }

    @PostMapping
    public ResponseEntity<Void> createPhishingAlert(@RequestBody PhishingAlertDto dto) {
        phishingAlertService.createPhishingAlert(dto);
        return ResponseEntity.ok().build();
    }
}
