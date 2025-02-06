package com.phishme.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// private static String generateRandomKey() {
	// byte[] keyBytes = new byte[32]; // 256-bit key
	// new SecureRandom().nextBytes(keyBytes);
	// return Base64.getEncoder().encodeToString(keyBytes);
	// }
}
