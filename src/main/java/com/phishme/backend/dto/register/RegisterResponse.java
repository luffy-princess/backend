package com.phishme.backend.dto.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private boolean registerResult;
    private RegisterErrors errors;
}
