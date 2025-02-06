package com.phishme.backend.dto.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterErrors {
    private String email;
    private String nickname;
    private String gender;
    private String birthdate;
    private String admissionyear;
    private String department;
    private String term;

    public RegisterErrors() {
        this.email = "";
        this.nickname = "";
        this.gender = "";
        this.birthdate = "";
        this.admissionyear = "";
        this.department = "";
        this.term = "";
    }

    public boolean areAllFieldsEmpty() {
        return (email.isEmpty() &&
                nickname.isEmpty() &&
                gender.isEmpty() &&
                birthdate.isEmpty() &&
                admissionyear.isEmpty() &&
                department.isEmpty() &&
                term.isEmpty());
    }
}