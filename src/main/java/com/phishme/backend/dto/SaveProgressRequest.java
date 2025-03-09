package com.phishme.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveProgressRequest {
    private String scenarioId;
    private int score;
}
