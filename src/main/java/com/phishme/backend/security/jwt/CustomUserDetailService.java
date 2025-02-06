package com.phishme.backend.security.jwt;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.phishme.backend.entities.Users;
import com.phishme.backend.enums.Messages;
import com.phishme.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserPrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("%s (username: %s)",
                        Messages.JWT_USER_NOT_FOUND.getMessage(), username)));

        return new UserPrincipal(user);
    }
}
