package com.phishme.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Messages {
    // 서비스 메세지
    AUTHENTICATION_SUCCESS("로그인에 성공하였습니다."),
    REGISTRATION_SUCCESS("회원가입에 성공하였습니다."),
    REGISTRATION_FAILED("회원가입에 실패하였습니다."),

    // 데이터 메세지
    USER_NEED_REGISTRATION("needRegistration"),
    INVALID_EMAIL("invalidEmail"),
    INVALID_GENDER_TYPE("invalidGenderType"),
    INVALID_NICKNAME_TOO_LONG("invalidNicknameTooLong"),
    INVALID_NICKNAME_ALREADY_IN_USE("invalidNicknameAlreadyInUse"),
    INVALID_BADWORD_NICKNAME("invalidBadWordNickname"),
    INVALID_BIRTHDATE("invalidBirthDate"),
    INVALID_ADMISSION_YEAR("invalidAdmissionYear"),
    INVALID_DEPARTMENT_NOT_EXIST("invalidDepartmentNotExist"),
    INVALID_TERM_AGREEMENT("invalidTermAgreement"),

    // JWT 인증 / 인가 메세지
    NOT_AUTHENTICATED_USER("인증되지 않은 유저입니다."),

    // 백엔드 JWT 로그 메세지
    INVALID_EXPIRED_TOKEN("만료된 토큰 입니다."),
    INVALID_TOKEN("유효하지않은 토큰 입니다."),
    JWT_USER_NOT_FOUND("유저를 찾을 수 없습니다.");

    private final String message;
}
