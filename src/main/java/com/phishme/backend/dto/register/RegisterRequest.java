package com.phishme.backend.dto.register;

import com.phishme.backend.enums.AuthProviderType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterRequest {
    private String idToken;
    private AuthProviderType authProvider;
    private String email;
    private String nickname;
    private String gender;
    private String birthdate;
    private String admissionyear;
    private String department;
    private TermAgreementDto[] termAgreement;
}
