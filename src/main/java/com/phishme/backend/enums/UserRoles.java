package com.phishme.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoles {
    NEED_TERM_AGREEMENT("ROLE_NEED_TERM_AGREEMENT", "약관 동의가 필요한 사용자"),
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
