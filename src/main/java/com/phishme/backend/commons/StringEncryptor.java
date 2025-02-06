package com.phishme.backend.commons;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.phishme.backend.security.enc.EncryptionProperties;

import jakarta.persistence.AttributeConverter;

@Component
public class StringEncryptor implements AttributeConverter<String, String> {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String DELIMITER = ":";
    private final SecretKey key;

    @Autowired
    public StringEncryptor(EncryptionProperties properties) {
        try {
            this.key = new SecretKeySpec(Base64.getDecoder().decode(properties.getKey()), "AES");
        } catch (Exception e) {
            throw new RuntimeException("암호화 초기화 실패", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null)
            return null;
        try {
            IvParameterSpec iv = new IvParameterSpec(key.toString().substring(0, 16).getBytes());

            // Encrypt
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data
            String ivBase64 = Base64.getEncoder().encodeToString(iv.getIV());
            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

            return ivBase64 + DELIMITER + encryptedBase64;
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        try {
            // Split IV and encrypted data
            String[] parts = dbData.split(DELIMITER);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid encrypted data format");
            }

            // Decode IV and encrypted data
            byte[] ivBytes = Base64.getDecoder().decode(parts[0]);
            byte[] encryptedBytes = Base64.getDecoder().decode(parts[1]);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            // Decrypt
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }
}