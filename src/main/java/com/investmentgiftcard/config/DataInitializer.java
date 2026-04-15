package com.investmentgiftcard.config;

import com.investmentgiftcard.model.entity.Role;
import com.investmentgiftcard.model.entity.User;
import com.investmentgiftcard.repository.RoleRepository;
import com.investmentgiftcard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Data initializer for development environment.
 * Seeds the database with test data on startup.
 */
@Slf4j
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            log.info("Initializing development data...");

            // Create default roles
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> {
                        Role role = Role.builder()
                                .name("USER")
                                .description("Default user role")
                                .build();
                        return roleRepository.save(role);
                    });

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role role = Role.builder()
                                .name("ADMIN")
                                .description("Administrator role")
                                .build();
                        return roleRepository.save(role);
                    });

            // Create test user if not exists
            if (!userRepository.existsByEmail("test@example.com")) {
                User testUser = User.builder()
                        .email("test@example.com")
                        .password(passwordEncoder.encode("Test@123456"))
                        .firstName("John")
                        .lastName("Doe")
                        .emailVerified(true)
                        .build();
                testUser.addRole(userRole);
                userRepository.save(testUser);
                log.info("Created test user: test@example.com / Test@123456");
            }

            // Create admin user if not exists
            if (!userRepository.existsByEmail("admin@example.com")) {
                User adminUser = User.builder()
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("Admin@123456"))
                        .firstName("Admin")
                        .lastName("User")
                        .emailVerified(true)
                        .build();
                adminUser.addRole(userRole);
                adminUser.addRole(adminRole);
                userRepository.save(adminUser);
                log.info("Created admin user: admin@example.com / Admin@123456");
            }

            log.info("Development data initialization complete.");
        };
    }
}
