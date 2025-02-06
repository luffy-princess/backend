package com.phishme.backend.security.enc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.encryption")
@Getter
@Setter
public class EncryptionProperties {
    private String key;
}