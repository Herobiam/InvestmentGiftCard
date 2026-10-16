package com.investmentgiftcard.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new Gift Card
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardRequest {

    // Section A: Buyer Information
    @NotBlank(message = "Buyer full name is required")
    @Size(max = 200, message = "Buyer full name must not exceed 200 characters")
    private String buyerFullName;

    @NotBlank(message = "Buyer ID/Passport is required")
    @Size(max = 50, message = "Buyer ID/Passport must not exceed 50 characters")
    private String buyerIdPassport;

    @NotBlank(message = "Buyer mobile number is required")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,20}$", message = "Invalid mobile number format")
    private String buyerMobile;

    @NotBlank(message = "Buyer address is required")
    private String buyerAddress;

    // Section B: Beneficiary Information
    @NotBlank(message = "Beneficiary full name is required")
    @Size(max = 200, message = "Beneficiary full name must not exceed 200 characters")
    private String beneficiaryFullName;

    @NotNull(message = "Beneficiary date of birth is required")
    @Past(message = "Beneficiary date of birth must be in the past")
    private LocalDate beneficiaryDob;

    @NotBlank(message = "Beneficiary mobile number is required")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{10,20}$", message = "Invalid mobile number format")
    private String beneficiaryMobile;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String beneficiaryEmail;

    @NotBlank(message = "Relationship to buyer is required")
    private String relationship;

    @NotBlank(message = "Preferred contact method is required")
    private String contactMethod;

    // Section C: Voucher Details
    @NotNull(message = "Total value is required")
    @DecimalMin(value = "500.00", message = "Total value must be at least N$ 500")
    private BigDecimal totalValue;

    @NotBlank(message = "Batch number is required")
    @Size(max = 50, message = "Batch number must not exceed 50 characters")
    private String batchNumber;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    // Section D: Payment Details
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String fnbAccountNumber;

    private String branch;
    
    // KYC Status
    private Boolean kycVerified = false;
}
