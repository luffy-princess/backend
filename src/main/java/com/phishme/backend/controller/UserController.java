package com.phishme.backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phishme.backend.dto.user.MyInfoResponse;
import com.phishme.backend.entities.Users;
import com.phishme.backend.enums.ErrorCode;
import com.phishme.backend.enums.UserRoles;
import com.phishme.backend.exceptions.BusinessException;
import com.phishme.backend.security.jwt.UserPrincipal;

@RestController
public class UserController {
    @GetMapping("/@me")
    public MyInfoResponse getMyInfoController(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Users user = userPrincipal.getUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_DOESNT_EXIST);
        }

        boolean isAdmin = user.getRole().equals(UserRoles.ADMIN.getKey());
        MyInfoResponse myInfoResponse = new MyInfoResponse().builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .isAdmin(isAdmin)
                .build();

        return myInfoResponse;
    }
}
