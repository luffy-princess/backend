package com.phishme.backend.dto;

import com.phishme.backend.entities.PhishingAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhishingAlertDto {
    private String icon;
    private String iconColor;
    private String title;
    private String description;

    public static PhishingAlertDto fromEntity(PhishingAlert entity) {
        return PhishingAlertDto.builder()
                .icon(entity.getIcon())
                .iconColor(entity.getIconColor())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .build();
    }
}
