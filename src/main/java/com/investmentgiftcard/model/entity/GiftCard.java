package com.investmentgiftcard.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Gift Card entity representing an investment gift card voucher.
 */
@Entity
@Table(name = "gift_cards", indexes = {
        @Index(name = "idx_gift_card_batch_number", columnList = "batch_number", unique = true),
        @Index(name = "idx_gift_card_reference", columnList = "reference_number", unique = true),
        @Index(name = "idx_gift_card_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftCard extends BaseEntity {

    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @Column(name = "batch_number", nullable = false, unique = true, length = 50)
    private String batchNumber;

    // Buyer Information (Section A)
    @Column(name = "buyer_full_name", nullable = false, length = 200)
    private String buyerFullName;

    @Column(name = "buyer_id_passport", nullable = false, length = 50)
    private String buyerIdPassport;

    @Column(name = "buyer_mobile", nullable = false, length = 20)
    private String buyerMobile;

    @Column(name = "buyer_address", nullable = false, columnDefinition = "TEXT")
    private String buyerAddress;

    // Beneficiary Information (Section B)
    @Column(name = "beneficiary_full_name", nullable = false, length = 200)
    private String beneficiaryFullName;

    @Column(name = "beneficiary_dob", nullable = false)
    private LocalDate beneficiaryDob;

    @Column(name = "beneficiary_mobile", nullable = false, length = 20)
    private String beneficiaryMobile;

    @Column(name = "beneficiary_email", length = 100)
    private String beneficiaryEmail;

    @Column(name = "relationship", nullable = false, length = 50)
    private String relationship;

    @Column(name = "contact_method", nullable = false, length = 20)
    private String contactMethod;

    // Voucher Details (Section C)
    @Column(name = "total_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalValue;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    // Payment Details (Section D)
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "fnb_account_number", length = 30)
    private String fnbAccountNumber;

    @Column(name = "branch", length = 100)
    private String branch;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private GiftCardStatus status = GiftCardStatus.PENDING_KYC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    /**
     * Gift Card Status Enum
     */
    public enum GiftCardStatus {
        PENDING_KYC("Pending KYC"),
        CARD_ISSUED("Card Issued"),
        REDEEMED("Redeemed"),
        CANCELLED("Cancelled"),
        EXPIRED("Expired");

        private final String displayName;

        GiftCardStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Check if the gift card is expired
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Check if the gift card can be redeemed
     */
    public boolean canBeRedeemed() {
        return status == GiftCardStatus.CARD_ISSUED && !isExpired();
    }
}
