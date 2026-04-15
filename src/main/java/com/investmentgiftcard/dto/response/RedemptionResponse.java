package com.investmentgiftcard.dto.response;

import com.investmentgiftcard.model.entity.Redemption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Redemption data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionResponse {

    private UUID id;
    private UUID giftCardId;
    private String giftCardReference;

    // Section E: Authentication details
    private String providedBuyerName;
    private String providedBuyerPhone;
    private String verificationStatus;
    private String verificationStatusDisplayName;
    private LocalDateTime verificationCallTime;
    private String verificationNotes;
    private String verifiedBy;

    // Section F: Product Selection
    private String selectedProduct;
    private String selectedProductDisplayName;
    private String otherProductDescription;
    private String accountName;
    private String accountNumber;

    // Redemption tracking
    private LocalDateTime redemptionDate;
    private String processedByUserName;
    private Boolean beneficiarySignatureCaptured;

    // Audit
    private LocalDateTime createdAt;

    /**
     * Create a RedemptionResponse from a Redemption entity
     */
    public static RedemptionResponse fromEntity(Redemption redemption) {
        return RedemptionResponse.builder()
                .id(redemption.getId())
                .giftCardId(redemption.getGiftCard().getId())
                .giftCardReference(redemption.getGiftCard().getReferenceNumber())
                .providedBuyerName(redemption.getProvidedBuyerName())
                .providedBuyerPhone(redemption.getProvidedBuyerPhone())
                .verificationStatus(redemption.getVerificationStatus().name())
                .verificationStatusDisplayName(redemption.getVerificationStatus().getDisplayName())
                .verificationCallTime(redemption.getVerificationCallTime())
                .verificationNotes(redemption.getVerificationNotes())
                .verifiedBy(redemption.getVerifiedBy())
                .selectedProduct(redemption.getSelectedProduct().name())
                .selectedProductDisplayName(redemption.getSelectedProduct().getDisplayName())
                .otherProductDescription(redemption.getOtherProductDescription())
                .accountName(redemption.getAccountName())
                .accountNumber(redemption.getAccountNumber())
                .redemptionDate(redemption.getRedemptionDate())
                .processedByUserName(redemption.getProcessedByUser() != null ? 
                        redemption.getProcessedByUser().getFullName() : null)
                .beneficiarySignatureCaptured(redemption.getBeneficiarySignatureCaptured())
                .createdAt(redemption.getCreatedAt())
                .build();
    }
}
