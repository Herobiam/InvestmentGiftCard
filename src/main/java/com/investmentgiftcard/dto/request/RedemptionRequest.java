package com.investmentgiftcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for initiating Gift Card Redemption
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionRequest {

    @NotNull(message = "Gift card ID is required")
    private UUID giftCardId;

    // Section E: Redemption & Authentication
    @NotBlank(message = "Buyer name is required for verification")
    @Size(max = 200, message = "Buyer name must not exceed 200 characters")
    private String providedBuyerName;

    @NotBlank(message = "Buyer phone number is required for verification")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,20}$", message = "Invalid phone number format")
    private String providedBuyerPhone;

    // Section F: Product Selection
    @NotBlank(message = "Selected investment product is required")
    private String selectedProduct;

    @Size(max = 200, message = "Other product description must not exceed 200 characters")
    private String otherProductDescription;

    @NotBlank(message = "Account name is required")
    @Size(max = 200, message = "Account name must not exceed 200 characters")
    private String accountName;

    @NotBlank(message = "Account number is required")
    @Size(max = 50, message = "Account number must not exceed 50 characters")
    private String accountNumber;
}
