package com.phishme.backend.service;

import com.phishme.backend.dto.PhishingAlertDto;
import com.phishme.backend.entities.PhishingAlert;
import com.phishme.backend.repositories.PhishingAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhishingAlertService {
    private final PhishingAlertRepository phishingAlertRepository;

    public List<PhishingAlertDto> getCurrentPhishingAlerts() {
        return phishingAlertRepository.findAll().stream()
                .map(PhishingAlertDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createPhishingAlert(PhishingAlertDto dto) {
        PhishingAlert alert = new PhishingAlert();
        alert.setIcon(dto.getIcon());
        alert.setIconColor(dto.getIconColor());
        alert.setTitle(dto.getTitle());
        alert.setDescription(dto.getDescription());
        phishingAlertRepository.save(alert);
    }
}
