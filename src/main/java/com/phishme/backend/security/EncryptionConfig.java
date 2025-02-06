package com.phishme.backend.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.phishme.backend.commons.StringEncryptor;
import com.phishme.backend.security.enc.EncryptionProperties;

@Configuration
@EnableConfigurationProperties(EncryptionProperties.class)
public class EncryptionConfig {

    @Bean
    public StringEncryptor stringEncryptor(EncryptionProperties properties) {
        return new StringEncryptor(properties);
    }
}