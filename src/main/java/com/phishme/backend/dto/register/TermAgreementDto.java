package com.phishme.backend.dto.register;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TermAgreementDto {
    private Long termId;
    private Integer agreedVersion;
    private Boolean isChecked;
}
