package com.investmentgiftcard.service;

import com.investmentgiftcard.dto.request.GiftCardRequest;
import com.investmentgiftcard.dto.request.RedemptionRequest;
import com.investmentgiftcard.dto.request.VerificationRequest;
import com.investmentgiftcard.dto.response.GiftCardResponse;
import com.investmentgiftcard.dto.response.RedemptionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Gift Card operations
 */
public interface GiftCardService {

    /**
     * Create a new gift card
     */
    GiftCardResponse createGiftCard(GiftCardRequest request);

    /**
     * Get gift card by ID
     */
    GiftCardResponse getGiftCardById(UUID id);

    /**
     * Get gift card by reference number
     */
    GiftCardResponse getGiftCardByReference(String referenceNumber);

    /**
     * Get all gift cards with pagination
     */
    Page<GiftCardResponse> getAllGiftCards(Pageable pageable);

    /**
     * Search gift cards
     */
    Page<GiftCardResponse> searchGiftCards(String searchTerm, Pageable pageable);

    /**
     * Get recent gift cards
     */
    List<GiftCardResponse> getRecentGiftCards();

    /**
     * Update gift card status
     */
    GiftCardResponse updateStatus(UUID id, String status);

    /**
     * Cancel gift card
     */
    GiftCardResponse cancelGiftCard(UUID id);

    /**
     * Initiate redemption process
     */
    RedemptionResponse initiateRedemption(RedemptionRequest request);

    /**
     * Update verification status
     */
    RedemptionResponse updateVerificationStatus(VerificationRequest request);

    /**
     * Complete redemption (after verification)
     */
    RedemptionResponse completeRedemption(UUID redemptionId);

    /**
     * Get redemption by gift card ID
     */
    RedemptionResponse getRedemptionByGiftCardId(UUID giftCardId);

    /**
     * Generate unique reference number
     */
    String generateReferenceNumber();
}
