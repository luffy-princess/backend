package com.phishme.backend.dto.user;

import java.util.List;

import com.phishme.backend.entities.TermAgreement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String gender;
    private String birthDate;
    private boolean isAdmin;
}
