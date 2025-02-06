package com.phishme.backend.service;

import com.phishme.backend.entities.Users;

public interface UserService {
    public Users findById(Long identifier);

    public Users findByUserEmail(String userEmail);

    public Users findByNickName(String nickName);

    public Users registerUser(Users user);
}
