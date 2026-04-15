package com.investmentgiftcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for updating verification status of a redemption
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {

    @NotNull(message = "Redemption ID is required")
    private UUID redemptionId;

    @NotBlank(message = "Verification status is required")
    private String verificationStatus;

    private String verificationNotes;
}
