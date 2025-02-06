package com.phishme.backend.commons;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.phishme.backend.enums.AuthProviderType;
import com.phishme.backend.enums.ErrorCode;
import com.phishme.backend.exceptions.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OIDCJwtValidator {

    private final Map<String, JwkProvider> jwkProviders = new ConcurrentHashMap<>();

    private String kakaoClientId;
    private String appleClientId;
    private String googleIosClientId;
    private String googleAndroidClientId;

    public OIDCJwtValidator(
            @Value("${oidc.kakao-client-id}") String kakaoClientId,
            @Value("${oidc.apple-client-id}") String appleClientId,
            @Value("${oidc.google-ios-client-id}") String googleIosClientId,
            @Value("${oidc.google-android-client-id}") String googleAndroidClientId) {

        this.kakaoClientId = kakaoClientId;
        this.appleClientId = appleClientId;
        this.googleIosClientId = googleIosClientId;
        this.googleAndroidClientId = googleAndroidClientId;

        try {
            jwkProviders.put(AuthProviderType.KAKAO.name(),
                    new JwkProviderBuilder("https://kauth.kakao.com")
                            .cached(10, 7, TimeUnit.DAYS).build());

            jwkProviders.put(AuthProviderType.GOOGLE.name(),
                    new JwkProviderBuilder(new URL(
                            "https://www.googleapis.com/oauth2/v3/certs"))
                            .cached(10, 7, TimeUnit.DAYS).build());

            jwkProviders.put(AuthProviderType.APPLE.name(),
                    new JwkProviderBuilder("https://appleid.apple.com/auth/keys")
                            .cached(10, 7, TimeUnit.DAYS).build());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public DecodedJWT validate(String idToken, String provider) {
        if (!jwkProviders.containsKey(provider)) {
            throw new BusinessException(ErrorCode.UNSUPORTED_PROVIDER);
        }

        DecodedJWT decodedJWT = JWT.decode(idToken);

        if (decodedJWT.getKeyId() == null) {
            throw new BusinessException(ErrorCode.INVALID_NON_KID_TOKEN);
        }

        JwkProvider jwkProvider = jwkProviders.get(provider);

        Jwk jwk;
        try {
            jwk = jwkProvider.get(decodedJWT.getKeyId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.FAILED_TO_VALIDATE_ID_TOKEN);
        }

        Algorithm algorithm;
        try {
            algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.FAILED_TO_VALIDATE_ID_TOKEN);
        }

        if (decodedJWT.getExpiresAt() == null || decodedJWT.getExpiresAt().before(new java.util.Date())) {
            throw new BusinessException(ErrorCode.INVALID_EXPIRED_ID_TOKEN);
        }

        if (decodedJWT.getNotBefore() != null && decodedJWT.getNotBefore().after(new java.util.Date())) {
            throw new BusinessException(ErrorCode.INVALID_DATE_ID_TOKEN);
        }

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(getExpectedIssuer(provider))
                .build();

        DecodedJWT verifiedToken = verifier.verify(idToken);

        validateAudience(decodedJWT.getAudience(), provider);

        return verifiedToken;
    }

    private String getExpectedIssuer(String provider) {
        switch (provider) {
            case "KAKAO":
                return "https://kauth.kakao.com";
            case "GOOGLE":
                return "https://accounts.google.com";
            case "APPLE":
                return "https://appleid.apple.com";
            default:
                throw new BusinessException(ErrorCode.UNSUPORTED_PROVIDER);
        }
    }

    private void validateAudience(Iterable<String> audiences, String provider) {
        switch (provider) {
            case "KAKAO":
                if (!containsAudience(audiences, kakaoClientId)) {
                    throw new BusinessException(ErrorCode.INVALID_AUD_ID_TOKEN);
                }
                break;
            case "GOOGLE":
                if (!containsAudience(audiences, googleIosClientId)
                        && !containsAudience(audiences, googleAndroidClientId)) {
                    throw new BusinessException(ErrorCode.INVALID_AUD_ID_TOKEN);
                }
                break;
            case "APPLE":
                if (!containsAudience(audiences, appleClientId)) {
                    throw new BusinessException(ErrorCode.INVALID_AUD_ID_TOKEN);
                }
                break;
            default:
                throw new BusinessException(ErrorCode.UNSUPORTED_PROVIDER);
        }
    }

    private boolean containsAudience(Iterable<String> audiences, String expectedAudience) {
        for (String audience : audiences) {
            if (audience.equals(expectedAudience)) {
                return true;
            }
        }
        return false;
    }
}