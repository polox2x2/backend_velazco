package com.velazco.velazco_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VelazcoBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(VelazcoBackApplication.class, args);

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "110305";
		String encodedPassword = passwordEncoder.encode(rawPassword);

		System.out.println("Contraseña codificada: " + encodedPassword);
	}
}
