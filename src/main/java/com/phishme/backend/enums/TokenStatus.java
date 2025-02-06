package com.phishme.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TokenStatus {
    AUTHENTICATED, EXPIRED, INVALID
}
