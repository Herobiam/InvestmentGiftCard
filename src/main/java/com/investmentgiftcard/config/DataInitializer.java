package com.investmentgiftcard.config;

import com.investmentgiftcard.model.entity.GiftCard;
import com.investmentgiftcard.model.entity.Role;
import com.investmentgiftcard.model.entity.User;
import com.investmentgiftcard.repository.GiftCardRepository;
import com.investmentgiftcard.repository.RoleRepository;
import com.investmentgiftcard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data initializer that seeds the database with initial data on startup.
 * Works for both H2 (dev) and PostgreSQL (production) databases.
 * Only inserts data if it doesn't already exist.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, RoleRepository roleRepository, GiftCardRepository giftCardRepository) {
        return args -> {
            log.info("=== Database Initialization Started ===");

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
            User testUser = null;
            if (!userRepository.existsByEmail("hero@example.com")) {
                testUser = User.builder()
                        .email("hero@example.com")
                        .password(passwordEncoder.encode("Test@123456"))
                        .firstName("Herobiam")
                        .lastName("Heita")
                        .emailVerified(true)
                        .build();
                testUser.addRole(userRole);
                testUser = userRepository.save(testUser);
                log.info("Created test user: hero@example.com / Test@123456");
            } else {
                testUser = userRepository.findByEmail("hero@example.com").orElse(null);
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

            // Create sample gift cards for testing
            if (giftCardRepository.count() == 0 && testUser != null) {
                final User creator = testUser;
                
                GiftCard card1 = GiftCard.builder()
                        .referenceNumber("GC-2026-001")
                        .batchNumber("BATCH-001")
                        .buyerFullName("John Smith")
                        .buyerIdPassport("12345678901")
                        .buyerMobile("+264 81 123 4567")
                        .buyerAddress("123 Main Street, Windhoek, Namibia")
                        .beneficiaryFullName("Sarah Smith")
                        .beneficiaryDob(LocalDate.of(1995, 5, 15))
                        .beneficiaryMobile("+264 81 987 6543")
                        .relationship("Daughter")
                        .contactMethod("SMS")
                        .totalValue(new BigDecimal("5000.00"))
                        .expiryDate(LocalDate.now().plusYears(1))
                        .paymentMethod("EFT")
                        .fnbAccountNumber("62123456789")
                        .branch("Windhoek Main")
                        .status(GiftCard.GiftCardStatus.CARD_ISSUED)
                        .createdByUser(creator)
                        .build();
                giftCardRepository.save(card1);

                GiftCard card2 = GiftCard.builder()
                        .referenceNumber("GC-2026-002")
                        .batchNumber("BATCH-002")
                        .buyerFullName("Maria Johnson")
                        .buyerIdPassport("98765432109")
                        .buyerMobile("+264 81 555 1234")
                        .buyerAddress("456 Independence Ave, Swakopmund, Namibia")
                        .beneficiaryFullName("Peter Johnson")
                        .beneficiaryDob(LocalDate.of(2000, 8, 22))
                        .beneficiaryMobile("+264 81 555 5678")
                        .relationship("Son")
                        .contactMethod("CALL")
                        .totalValue(new BigDecimal("10000.00"))
                        .expiryDate(LocalDate.now().plusMonths(6))
                        .paymentMethod("CASH")
                        .fnbAccountNumber(null)
                        .branch("Swakopmund")
                        .status(GiftCard.GiftCardStatus.PENDING_KYC)
                        .createdByUser(creator)
                        .build();
                giftCardRepository.save(card2);

                GiftCard card3 = GiftCard.builder()
                        .referenceNumber("GC-2026-003")
                        .batchNumber("BATCH-003")
                        .buyerFullName("David Williams")
                        .buyerIdPassport("55667788990")
                        .buyerMobile("+264 81 222 3333")
                        .buyerAddress("789 Palm Road, Oshakati, Namibia")
                        .beneficiaryFullName("Emma Williams")
                        .beneficiaryDob(LocalDate.of(1998, 12, 1))
                        .beneficiaryMobile("+264 81 444 5555")
                        .relationship("Sister")
                        .contactMethod("EMAIL")
                        .totalValue(new BigDecimal("2500.00"))
                        .expiryDate(LocalDate.now().minusDays(30))
                        .paymentMethod("EFT")
                        .fnbAccountNumber("62987654321")
                        .branch("Oshakati")
                        .status(GiftCard.GiftCardStatus.REDEEMED)
                        .createdByUser(creator)
                        .build();
                giftCardRepository.save(card3);

                log.info("Created 3 sample gift cards for testing");
            }

            log.info("=== Database Initialization Complete ===");
        };
    }
}
