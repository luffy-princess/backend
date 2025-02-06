package com.phishme.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 서비스 에러
    INVALID_SERVICE_REQUEST(400, "SERVICCE-001", "잘못된 요청 방식입니다."),
    SERVICE_UNKOWN_ERROR_OCCURED(500, "SERVICE-999", "알 수 없는 오류가 발생했습니다."),

    // 유저 에러
    USER_DOESNT_EXIST(404, "USER-001", "유저를 찾을 수 없습니다."),
    NOT_AUTHENTICATED_USER(401, "USER-003", "인증되지 않은 유저입니다."),
    EMAIL_ALREADY_REGISTERED(400, "USER-004", "이미 등록된 이메일 주소 입니다."),
    USER_LOGOUT(403, "USER-999", "토큰 만료로 로그아웃 처리 되었습니다."),

    // 비속어 필터링 에러
    BADWORD_REQUEST_FAILED(400, "BADWORD-001", "비속어 필터링 요청에 실패했습니다."),

    // OIDC 에러
    INVALID_NON_KID_TOKEN(400, "OIDC-001", "유효하지 않은 ID토큰 입니다."),
    INVALID_EXPIRED_ID_TOKEN(400, "OIDC-002", "유효하지 않은 ID토큰 입니다."),
    INVALID_DATE_ID_TOKEN(400, "OIDC-003", "유효하지 않은 ID토큰 입니다."),
    INVALID_AUD_ID_TOKEN(400, "OIDC-004", "유효하지 않은 ID토큰 입니다."),
    UNSUPORTED_PROVIDER(400, "OIDC-005", "유효하지 않은 인증 제공자 입니다."),
    FAILED_TO_VALIDATE_ID_TOKEN(401, "OIDC-006", "ID토큰 유효성 검사에 실패했습니다."),

    // JWT 에러
    INVALID_JWT(400, "JWT-001", "유효하지않은 JWT토큰 입니다."),
    INVALID_EXPIRED_TOKEN(400, "JWT-002", "만료된 JWT토큰 입니다."),
    MISSING_JWT_TOKEN(404, "JWT-003", "JWT토큰이 누락되었습니다."),
    JWT_USER_NOT_FOUND(404, "JWT-004", "JWT토큰 생성을 위한 유저 정보를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
