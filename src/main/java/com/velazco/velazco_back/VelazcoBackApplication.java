package com.velazco.velazco_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableAsync
public class VelazcoBackApplication {

	@PostConstruct
	public void init() {
		// Forzar a que todo el sistema backend use la hora de Perú (GMT-5)
		TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
	}

	public static void main(String[] args) {
		SpringApplication.run(VelazcoBackApplication.class, args);

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "110305";
		String encodedPassword = passwordEncoder.encode(rawPassword);

		System.out.println("Contraseña codificada: " + encodedPassword);
	}
}
