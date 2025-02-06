package com.phishme.backend.dto.login;

import com.phishme.backend.enums.AuthProviderType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {
    private String idToken;
    private AuthProviderType authProvider;
}
