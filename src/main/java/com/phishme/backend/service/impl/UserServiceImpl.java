package com.phishme.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phishme.backend.entities.Users;
import com.phishme.backend.repositories.UserRepository;
import com.phishme.backend.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Users findById(Long identifier) {
        return userRepository.findById(identifier)
                .orElse(null);
    }

    @Override
    public Users findByUserEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElse(null);
    }

    @Override
    public Users findByNickName(String nickName) {
        return userRepository.findByNickname(nickName).orElse(null);
    }

    @Override
    public Users registerUser(Users user) {
        return userRepository.save(user);
    }
}
