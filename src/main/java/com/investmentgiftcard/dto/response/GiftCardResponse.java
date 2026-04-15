package com.investmentgiftcard.dto.response;

import com.investmentgiftcard.model.entity.GiftCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for Gift Card data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardResponse {

    private UUID id;
    private String referenceNumber;
    private String batchNumber;

    // Buyer Information
    private String buyerFullName;
    private String buyerIdPassport;
    private String buyerMobile;
    private String buyerAddress;

    // Beneficiary Information
    private String beneficiaryFullName;
    private LocalDate beneficiaryDob;
    private String beneficiaryMobile;
    private String relationship;
    private String contactMethod;

    // Voucher Details
    private BigDecimal totalValue;
    private LocalDate expiryDate;
    private boolean expired;

    // Payment Details
    private String paymentMethod;
    private String fnbAccountNumber;
    private String branch;

    // Status
    private String status;
    private String statusDisplayName;
    private boolean canBeRedeemed;

    // Audit
    private LocalDateTime createdAt;
    private String createdByUserName;

    // Redemption info (if redeemed)
    private RedemptionResponse redemption;

    /**
     * Create a GiftCardResponse from a GiftCard entity
     */
    public static GiftCardResponse fromEntity(GiftCard giftCard) {
        return GiftCardResponse.builder()
                .id(giftCard.getId())
                .referenceNumber(giftCard.getReferenceNumber())
                .batchNumber(giftCard.getBatchNumber())
                .buyerFullName(giftCard.getBuyerFullName())
                .buyerIdPassport(giftCard.getBuyerIdPassport())
                .buyerMobile(giftCard.getBuyerMobile())
                .buyerAddress(giftCard.getBuyerAddress())
                .beneficiaryFullName(giftCard.getBeneficiaryFullName())
                .beneficiaryDob(giftCard.getBeneficiaryDob())
                .beneficiaryMobile(giftCard.getBeneficiaryMobile())
                .relationship(giftCard.getRelationship())
                .contactMethod(giftCard.getContactMethod())
                .totalValue(giftCard.getTotalValue())
                .expiryDate(giftCard.getExpiryDate())
                .expired(giftCard.isExpired())
                .paymentMethod(giftCard.getPaymentMethod())
                .fnbAccountNumber(giftCard.getFnbAccountNumber())
                .branch(giftCard.getBranch())
                .status(giftCard.getStatus().name())
                .statusDisplayName(giftCard.getStatus().getDisplayName())
                .canBeRedeemed(giftCard.canBeRedeemed())
                .createdAt(giftCard.getCreatedAt())
                .createdByUserName(giftCard.getCreatedByUser() != null ? 
                        giftCard.getCreatedByUser().getFullName() : null)
                .build();
    }
}
