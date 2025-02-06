package com.phishme.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor()
public class BadWordFilterPost {
    private String text;
    private String mode;
}
