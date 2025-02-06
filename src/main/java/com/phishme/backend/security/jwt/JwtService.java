package com.phishme.backend.security.jwt;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.phishme.backend.entities.JwtTokens;
import com.phishme.backend.entities.Users;
import com.phishme.backend.enums.ErrorCode;
import com.phishme.backend.enums.TokenStatus;
import com.phishme.backend.exceptions.BusinessException;
import com.phishme.backend.repositories.JwtTokenRepository;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
public class JwtService {
    private final CustomUserDetailService customUserDetailService;
    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;
    private final JwtTokenRepository jwtTokenRepository;

    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;

    public JwtService(CustomUserDetailService customUserDetailService, JwtGenerator jwtGenerator, JwtUtil jwtUtil,
            JwtTokenRepository jwtTokenRepository, @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
            @Value("${jwt.refresh-secret}") String REFRESH_SECRET_KEY,
            @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
            @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION) {
        this.customUserDetailService = customUserDetailService;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.jwtTokenRepository = jwtTokenRepository;
        this.ACCESS_SECRET_KEY = jwtUtil.getSigningKey(ACCESS_SECRET_KEY);
        this.REFRESH_SECRET_KEY = jwtUtil.getSigningKey(REFRESH_SECRET_KEY);
        this.ACCESS_EXPIRATION = ACCESS_EXPIRATION;
        this.REFRESH_EXPIRATION = REFRESH_EXPIRATION;
    }

    public String generateAccessToken(HttpServletResponse response, Users requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
        response.setHeader("Authorization", "Bearer " + accessToken);

        return accessToken;
    }

    @Transactional
    public String generateRefreshToken(HttpServletResponse response, Users requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);

        response.setHeader("X-Refresh-Token", refreshToken);

        JwtTokens saveJwtToken = jwtTokenRepository.findByUserId(requestUser.getId())
                .map(existingToken -> {
                    existingToken.setRefreshToken(refreshToken);
                    return existingToken;
                })
                .orElseGet(() -> JwtTokens.builder()
                        .userId(requestUser.getId())
                        .refreshToken(refreshToken)
                        .build());
        jwtTokenRepository.save(saveJwtToken);

        return refreshToken;
    }

    public boolean validateAccessToken(String token) {
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }

    public boolean validateRefreshToken(String token, Long identifier) {
        boolean isRefreshValid = jwtUtil.getTokenStatus(token, REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;

        JwtTokens storedToken = jwtTokenRepository.findByUserId(identifier).orElse(null);
        if (storedToken == null) {
            return false;
        }

        boolean isTokenMatched = storedToken.getRefreshToken().equals(token);
        return isRefreshValid && isTokenMatched;
    }

    public String resolveTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.MISSING_JWT_TOKEN);
        }
        return authorizationHeader.substring("Bearer ".length());
    }

    public Authentication getAuthentication(String token) {
        UserPrincipal principal = customUserDetailService.loadUserByUsername(getUserPk(token, ACCESS_SECRET_KEY));
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    private String getUserPk(String token, Key secretKey) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getIdentifierFromRefresh(String refreshToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_JWT);
        }
    }

    public void logout(Users requestUser, HttpServletResponse response) {
        jwtTokenRepository.deleteByUserId(requestUser.getId());

        response.setHeader("Authorization", "");
        response.setHeader("X-Refresh-Token", "");

        throw new BusinessException(ErrorCode.USER_LOGOUT);
    }
}
