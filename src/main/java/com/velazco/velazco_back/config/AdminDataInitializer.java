package com.velazco.velazco_back.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.velazco.velazco_back.model.Role;
import com.velazco.velazco_back.model.User;
import com.velazco.velazco_back.repositories.RoleRepository;
import com.velazco.velazco_back.repositories.UserRepository;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminDataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Insertar roles si no existen
        List<String> roleNames = List.of("Administrador", "Cajero", "Vendedor", "Producción", "Entregas", "Cliente");
        for (String roleName : roleNames) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
        }

        // Crear usuario admin principal si no existe ninguno con ese correo
        String adminEmail = "admin@test.com"; // El mismo usado en data.sql
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            Role adminRole = roleRepository.findByName("Administrador")
                    .orElseThrow(() -> new RuntimeException("Error: Role Administrador no encontrado"));

            User admin = new User();
            admin.setName("Admin Principal");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin1234")); // Contraseña por defecto
            admin.setActive(true);
            admin.setRole(adminRole);

            userRepository.save(admin);
            System.out.println("=======================================================================");
            System.out.println("✅ Usuario Administrador inicializado exitosamente.");
            System.out.println("Email: " + adminEmail);
            System.out.println("Password: admin1234");
            System.out.println("=======================================================================");
        }
    }
}
