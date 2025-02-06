package com.phishme.backend.commons;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.phishme.backend.dto.BadWordFilterPost;
import com.phishme.backend.dto.BadWordFilterResponse;
import com.phishme.backend.dto.register.RegisterErrors;
import com.phishme.backend.dto.register.RegisterRequest;
import com.phishme.backend.dto.register.TermAgreementDto;
import com.phishme.backend.entities.Terms;
import com.phishme.backend.enums.ErrorCode;
import com.phishme.backend.enums.GenderType;
import com.phishme.backend.enums.Messages;
import com.phishme.backend.exceptions.BusinessException;
import com.phishme.backend.service.TermService;
import com.phishme.backend.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ValidateUtils {
    @Autowired
    private TermService termService;

    @Autowired
    private UserService userService;

    private String profanityApiKey;

    public ValidateUtils(@Value("${app.profanity.key}") String profanityApiKey) {
        this.profanityApiKey = profanityApiKey;
    }

    public void validateEmail(String receivedEmail, String userEmailFromIdToken, RegisterErrors errors) {
        if (!receivedEmail.equals(userEmailFromIdToken)) {
            errors.setEmail(Messages.INVALID_EMAIL.getMessage());
        }
    }

    public void validateNickname(String receivedNickname, RegisterErrors errors) {
        if (receivedNickname.length() > 6) {
            errors.setNickname(Messages.INVALID_NICKNAME_TOO_LONG.getMessage());
        } else if (userService.findByNickName(receivedNickname) != null) {
            errors.setNickname(Messages.INVALID_NICKNAME_ALREADY_IN_USE.getMessage());
        } else if (!checkBadwordNickname(receivedNickname)) {
            errors.setNickname(Messages.INVALID_BADWORD_NICKNAME.getMessage());
        }
    }

    public void validateBirthdate(String receivedBirthDate, RegisterErrors errors) {
        if (!isValidBirthDate(receivedBirthDate)) {
            errors.setBirthdate(Messages.INVALID_BIRTHDATE.getMessage());
        }
    }

    public void validateGenderType(String receivedGenderType, RegisterErrors errors) {
        if (!isValidGenderType(receivedGenderType)) {
            errors.setGender(Messages.INVALID_GENDER_TYPE.getMessage());
        }
    }

    public boolean isValidGenderType(String genderTypeData) {
        if (searchEnum(GenderType.class, genderTypeData) == null) {
            return false;
        }
        return true;
    }

    public void validateTermAgreement(RegisterRequest registerRequestDto, RegisterErrors errors) {
        for (TermAgreementDto termAgreementDto : registerRequestDto.getTermAgreement()) {
            if (!isValidTermAgreement(termAgreementDto)) {
                errors.setTerm(Messages.INVALID_TERM_AGREEMENT.getMessage());
                break;
            }
        }
    }

    private boolean isValidTermAgreement(TermAgreementDto termAgreementDto) {
        boolean isChecked = termAgreementDto.getIsChecked();

        if (!isChecked) {
            return false;
        }

        Terms term = termService.findById(termAgreementDto.getTermId());

        if (term == null) {
            return false;
        }

        int validVersion = term.getVersion();
        int requstedVersion = termAgreementDto.getAgreedVersion();

        if (validVersion != requstedVersion) {
            return false;
        }

        return true;
    }

    private boolean checkBadwordNickname(String nickname) {
        WebClient webClient = WebClient.builder().build();

        String url = "https://api.profanity-filter.run/api/v1/filter";

        BadWordFilterPost dto = new BadWordFilterPost();
        dto.setText(nickname);
        dto.setMode("NORMAL");

        BadWordFilterResponse response = webClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .header("x-api-key", profanityApiKey)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(BadWordFilterResponse.class)
                .block();

        if (response.getStatus().getCode() != 2000) {
            log.error(String.format("비속어 필터링 API 오류: %s", response.getStatus().getDescription()));
            throw new BusinessException(ErrorCode.BADWORD_REQUEST_FAILED);
        }

        log.info(String.format("비속어 필터링 API elapsed time: %s", response.getElapsed()));

        if (response.getDetected().size() > 0) {
            return false;
        }
        return true;
    }

    private boolean isValidBirthDate(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            LocalDate.parse(input, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private <T extends Enum<?>> T searchEnum(Class<T> enumeration,
            String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }
}
