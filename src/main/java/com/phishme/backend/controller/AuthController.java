package com.phishme.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.phishme.backend.commons.OIDCJwtValidator;
import com.phishme.backend.commons.ValidateUtils;
import com.phishme.backend.dto.login.LoginRequest;
import com.phishme.backend.dto.login.LoginResponse;
import com.phishme.backend.dto.register.RegisterErrors;
import com.phishme.backend.dto.register.RegisterRequest;
import com.phishme.backend.dto.register.RegisterResponse;
import com.phishme.backend.dto.register.TermAgreementDto;
import com.phishme.backend.entities.TermAgreement;
import com.phishme.backend.entities.Terms;
import com.phishme.backend.entities.Users;
import com.phishme.backend.enums.AuthProviderType;
import com.phishme.backend.enums.ErrorCode;
import com.phishme.backend.enums.Messages;
import com.phishme.backend.enums.UserRoles;
import com.phishme.backend.exceptions.BusinessException;
import com.phishme.backend.security.jwt.JwtService;
import com.phishme.backend.security.jwt.UserPrincipal;
import com.phishme.backend.service.TermService;
import com.phishme.backend.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
    @Autowired
    private OIDCJwtValidator oidcJwtValidator;

    @Autowired
    private ValidateUtils validateUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TermService termService;

    @PostMapping("/logout")
    public void logoutController(HttpServletResponse response, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Users user = userPrincipal.getUser();
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_DOESNT_EXIST);
        }

        jwtService.logout(user, response);
    }

    @PostMapping("/login")
    public LoginResponse loginController(HttpServletResponse response, @RequestBody LoginRequest loginDto) {
        String idToken = loginDto.getIdToken();
        AuthProviderType authProvider = loginDto.getAuthProvider();

        DecodedJWT jwt = oidcJwtValidator.validate(idToken, authProvider.name());
        String userEmail = jwt.getClaim("email").asString();

        Users user = userService.findByUserEmail(userEmail);
        if (user == null) {
            return new LoginResponse(Messages.USER_NEED_REGISTRATION.getMessage(), userEmail);
        }

        if (!user.getProvider().equals(authProvider.name())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        jwtService.generateAccessToken(response, user);
        jwtService.generateRefreshToken(response, user);

        return new LoginResponse(Messages.AUTHENTICATION_SUCCESS.getMessage(), userEmail);
    }

    @PostMapping("/register")
    public RegisterResponse registerController(@RequestBody RegisterRequest registerRequestDto) {
        // JWT 검증
        String idToken = registerRequestDto.getIdToken();
        AuthProviderType authProvider = registerRequestDto.getAuthProvider();

        DecodedJWT jwt = oidcJwtValidator.validate(idToken, authProvider.name());
        String userEmailFromIdToken = jwt.getClaim("email").asString();

        Users isUserAlreadyExist = userService.findByUserEmail(userEmailFromIdToken);
        if (isUserAlreadyExist != null) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        RegisterErrors errors = new RegisterErrors();

        // 이메일 검증
        String requestedEmail = registerRequestDto.getEmail();
        validateUtils.validateEmail(requestedEmail, userEmailFromIdToken, errors);
        // 닉네임 검증
        String requestedNickname = registerRequestDto.getNickname();
        validateUtils.validateNickname(requestedNickname, errors);
        // 생년월일 검증
        String requestedBirthdate = registerRequestDto.getBirthdate();
        validateUtils.validateBirthdate(requestedBirthdate, errors);
        // 성별 검증
        String requestedGender = registerRequestDto.getGender();
        validateUtils.validateGenderType(requestedGender, errors);
        // 약관 동의 검증
        TermAgreementDto[] requestedTermAgreement = registerRequestDto.getTermAgreement();
        validateUtils.validateTermAgreement(registerRequestDto, errors);

        if (!errors.areAllFieldsEmpty()) {
            return new RegisterResponse(Messages.REGISTRATION_FAILED.getMessage(), false, errors);
        }

        Users user = new Users().builder()
                .provider(authProvider.name())
                .email(userEmailFromIdToken)
                .nickname(requestedNickname)
                .gender(requestedGender)
                .birthDate(requestedBirthdate)
                .role(UserRoles.USER.getKey())
                .build();
        Users registeredUser = userService.registerUser(user);

        List<TermAgreement> termAgreements = new ArrayList<TermAgreement>();
        for (TermAgreementDto termAgreementDto : requestedTermAgreement) {
            Terms term = termService.findById(termAgreementDto.getTermId());
            TermAgreement newTermAgreement = new TermAgreement().builder()
                    .term(term)
                    .user(registeredUser)
                    .agreedVersion(termAgreementDto.getAgreedVersion())
                    .agreed(true)
                    .build();

            termAgreements.add(newTermAgreement);
        }
        termService.saveAllTermArgeements(termAgreements);

        return new RegisterResponse(Messages.REGISTRATION_SUCCESS.getMessage(), true, errors);
    }
}
