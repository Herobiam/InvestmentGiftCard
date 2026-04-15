package com.investmentgiftcard.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Redemption entity for tracking gift card redemptions.
 * Contains authentication verification and product selection details.
 */
@Entity
@Table(name = "redemptions", indexes = {
        @Index(name = "idx_redemption_gift_card", columnList = "gift_card_id"),
        @Index(name = "idx_redemption_verification_status", columnList = "verification_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Redemption extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_card_id", nullable = false)
    private GiftCard giftCard;

    // Section E: Redemption & Authentication
    @Column(name = "provided_buyer_name", nullable = false, length = 200)
    private String providedBuyerName;

    @Column(name = "provided_buyer_phone", nullable = false, length = 20)
    private String providedBuyerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "verification_call_time")
    private LocalDateTime verificationCallTime;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    // Section F: Product Selection
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_product", nullable = false, length = 30)
    private InvestmentProduct selectedProduct;

    @Column(name = "other_product_description", length = 200)
    private String otherProductDescription;

    @Column(name = "account_name", nullable = false, length = 200)
    private String accountName;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    // Redemption tracking
    @Column(name = "redemption_date")
    private LocalDateTime redemptionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id")
    private User processedByUser;

    @Column(name = "beneficiary_signature_captured")
    @Builder.Default
    private Boolean beneficiarySignatureCaptured = false;

    /**
     * Verification Status Enum
     */
    public enum VerificationStatus {
        PENDING("Pending Verification"),
        CALL_INITIATED("Call Initiated"),
        VERIFIED("Verified"),
        FAILED("Verification Failed"),
        BUYER_UNREACHABLE("Buyer Unreachable");

        private final String displayName;

        VerificationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Investment Product Enum
     */
    public enum InvestmentProduct {
        CALL_ACCOUNT("Call Account"),
        NOTICE_ACCOUNT("Notice Account"),
        MONEY_MAXIMISER("Money Maximiser"),
        OTHER("Other");

        private final String displayName;

        InvestmentProduct(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Mark verification as successful
     */
    public void markVerified(String verifiedBy, String notes) {
        this.verificationStatus = VerificationStatus.VERIFIED;
        this.verifiedBy = verifiedBy;
        this.verificationNotes = notes;
        this.redemptionDate = LocalDateTime.now();
    }

    /**
     * Mark verification as failed
     */
    public void markVerificationFailed(String reason) {
        this.verificationStatus = VerificationStatus.FAILED;
        this.verificationNotes = reason;
    }
}
