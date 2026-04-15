package com.investmentgiftcard.service.impl;

import com.investmentgiftcard.dto.request.GiftCardRequest;
import com.investmentgiftcard.dto.request.RedemptionRequest;
import com.investmentgiftcard.dto.request.VerificationRequest;
import com.investmentgiftcard.dto.response.GiftCardResponse;
import com.investmentgiftcard.dto.response.RedemptionResponse;
import com.investmentgiftcard.exception.BadRequestException;
import com.investmentgiftcard.exception.ConflictException;
import com.investmentgiftcard.exception.ResourceNotFoundException;
import com.investmentgiftcard.model.entity.GiftCard;
import com.investmentgiftcard.model.entity.GiftCard.GiftCardStatus;
import com.investmentgiftcard.model.entity.Redemption;
import com.investmentgiftcard.model.entity.Redemption.InvestmentProduct;
import com.investmentgiftcard.model.entity.Redemption.VerificationStatus;
import com.investmentgiftcard.model.entity.User;
import com.investmentgiftcard.repository.GiftCardRepository;
import com.investmentgiftcard.repository.RedemptionRepository;
import com.investmentgiftcard.repository.UserRepository;
import com.investmentgiftcard.service.GiftCardService;
import com.investmentgiftcard.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of GiftCardService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GiftCardServiceImpl implements GiftCardService {

    private final GiftCardRepository giftCardRepository;
    private final RedemptionRepository redemptionRepository;
    private final UserRepository userRepository;

    @Override
    public GiftCardResponse createGiftCard(GiftCardRequest request) {
        log.info("Creating gift card for buyer: {}", request.getBuyerFullName());

        // Check for duplicate batch number
        if (giftCardRepository.existsByBatchNumber(request.getBatchNumber())) {
            throw new ConflictException("Batch number already exists: " + request.getBatchNumber());
        }

        // Get current user
        User currentUser = getCurrentUser();

        // Generate unique reference number
        String referenceNumber = generateReferenceNumber();

        // Create gift card entity
        GiftCard giftCard = GiftCard.builder()
                .referenceNumber(referenceNumber)
                .batchNumber(request.getBatchNumber())
                .buyerFullName(request.getBuyerFullName())
                .buyerIdPassport(request.getBuyerIdPassport())
                .buyerMobile(request.getBuyerMobile())
                .buyerAddress(request.getBuyerAddress())
                .beneficiaryFullName(request.getBeneficiaryFullName())
                .beneficiaryDob(request.getBeneficiaryDob())
                .beneficiaryMobile(request.getBeneficiaryMobile())
                .relationship(request.getRelationship())
                .contactMethod(request.getContactMethod())
                .totalValue(request.getTotalValue())
                .expiryDate(request.getExpiryDate())
                .paymentMethod(request.getPaymentMethod())
                .fnbAccountNumber(request.getFnbAccountNumber())
                .branch(request.getBranch())
                .status(GiftCardStatus.PENDING_KYC)
                .createdByUser(currentUser)
                .build();

        giftCard.setCreatedBy(currentUser != null ? currentUser.getEmail() : "system");

        GiftCard savedGiftCard = giftCardRepository.save(giftCard);
        log.info("Gift card created with reference: {}", referenceNumber);

        return GiftCardResponse.fromEntity(savedGiftCard);
    }

    @Override
    @Transactional(readOnly = true)
    public GiftCardResponse getGiftCardById(UUID id) {
        GiftCard giftCard = findGiftCardById(id);
        GiftCardResponse response = GiftCardResponse.fromEntity(giftCard);
        
        // Include redemption info if exists
        redemptionRepository.findByGiftCardId(id)
                .ifPresent(redemption -> response.setRedemption(RedemptionResponse.fromEntity(redemption)));
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public GiftCardResponse getGiftCardByReference(String referenceNumber) {
        GiftCard giftCard = giftCardRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Gift card not found with reference: " + referenceNumber));
        
        GiftCardResponse response = GiftCardResponse.fromEntity(giftCard);
        
        // Include redemption info if exists
        redemptionRepository.findByGiftCardId(giftCard.getId())
                .ifPresent(redemption -> response.setRedemption(RedemptionResponse.fromEntity(redemption)));
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GiftCardResponse> getAllGiftCards(Pageable pageable) {
        return giftCardRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(GiftCardResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GiftCardResponse> searchGiftCards(String searchTerm, Pageable pageable) {
        return giftCardRepository.searchGiftCards(searchTerm, pageable)
                .map(GiftCardResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GiftCardResponse> getRecentGiftCards() {
        return giftCardRepository.findTop10ByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(GiftCardResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public GiftCardResponse updateStatus(UUID id, String status) {
        GiftCard giftCard = findGiftCardById(id);
        
        try {
            GiftCardStatus newStatus = GiftCardStatus.valueOf(status.toUpperCase());
            giftCard.setStatus(newStatus);
            giftCard.setUpdatedBy(getCurrentUserEmail());
            
            GiftCard updatedGiftCard = giftCardRepository.save(giftCard);
            log.info("Gift card {} status updated to {}", id, status);
            
            return GiftCardResponse.fromEntity(updatedGiftCard);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }

    @Override
    public GiftCardResponse cancelGiftCard(UUID id) {
        GiftCard giftCard = findGiftCardById(id);
        
        if (giftCard.getStatus() == GiftCardStatus.REDEEMED) {
            throw new BadRequestException("Cannot cancel a redeemed gift card");
        }
        
        giftCard.setStatus(GiftCardStatus.CANCELLED);
        giftCard.setUpdatedBy(getCurrentUserEmail());
        
        GiftCard cancelledGiftCard = giftCardRepository.save(giftCard);
        log.info("Gift card {} cancelled", id);
        
        return GiftCardResponse.fromEntity(cancelledGiftCard);
    }

    @Override
    public RedemptionResponse initiateRedemption(RedemptionRequest request) {
        log.info("Initiating redemption for gift card: {}", request.getGiftCardId());
        
        GiftCard giftCard = findGiftCardById(request.getGiftCardId());
        
        // Validate gift card can be redeemed
        if (!giftCard.canBeRedeemed()) {
            if (giftCard.isExpired()) {
                throw new BadRequestException("Gift card has expired");
            }
            throw new BadRequestException("Gift card cannot be redeemed. Current status: " + giftCard.getStatus().getDisplayName());
        }
        
        // Check if redemption already exists
        if (redemptionRepository.existsByGiftCardId(request.getGiftCardId())) {
            throw new ConflictException("Redemption already initiated for this gift card");
        }
        
        // Validate buyer information matches
        boolean buyerNameMatches = giftCard.getBuyerFullName().equalsIgnoreCase(request.getProvidedBuyerName().trim());
        boolean buyerPhoneMatches = normalizePhone(giftCard.getBuyerMobile()).equals(normalizePhone(request.getProvidedBuyerPhone()));
        
        if (!buyerNameMatches || !buyerPhoneMatches) {
            log.warn("Buyer verification mismatch for gift card {}. Name match: {}, Phone match: {}", 
                    request.getGiftCardId(), buyerNameMatches, buyerPhoneMatches);
        }
        
        // Get current user
        User currentUser = getCurrentUser();
        
        // Parse investment product
        InvestmentProduct product;
        try {
            product = InvestmentProduct.valueOf(request.getSelectedProduct().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid investment product: " + request.getSelectedProduct());
        }
        
        // Create redemption record
        Redemption redemption = Redemption.builder()
                .giftCard(giftCard)
                .providedBuyerName(request.getProvidedBuyerName())
                .providedBuyerPhone(request.getProvidedBuyerPhone())
                .verificationStatus(VerificationStatus.PENDING)
                .selectedProduct(product)
                .otherProductDescription(request.getOtherProductDescription())
                .accountName(request.getAccountName())
                .accountNumber(request.getAccountNumber())
                .processedByUser(currentUser)
                .build();
        
        redemption.setCreatedBy(currentUser != null ? currentUser.getEmail() : "system");
        
        Redemption savedRedemption = redemptionRepository.save(redemption);
        log.info("Redemption initiated with ID: {}", savedRedemption.getId());
        
        return RedemptionResponse.fromEntity(savedRedemption);
    }

    @Override
    public RedemptionResponse updateVerificationStatus(VerificationRequest request) {
        log.info("Updating verification status for redemption: {}", request.getRedemptionId());
        
        Redemption redemption = redemptionRepository.findById(request.getRedemptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Redemption not found: " + request.getRedemptionId()));
        
        // Parse verification status
        VerificationStatus newStatus;
        try {
            newStatus = VerificationStatus.valueOf(request.getVerificationStatus().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid verification status: " + request.getVerificationStatus());
        }
        
        redemption.setVerificationStatus(newStatus);
        redemption.setVerificationNotes(request.getVerificationNotes());
        redemption.setVerifiedBy(getCurrentUserEmail());
        
        if (newStatus == VerificationStatus.CALL_INITIATED) {
            redemption.setVerificationCallTime(LocalDateTime.now());
        }
        
        // If verified, complete the redemption
        if (newStatus == VerificationStatus.VERIFIED) {
            redemption.setRedemptionDate(LocalDateTime.now());
            
            // Update gift card status to redeemed
            GiftCard giftCard = redemption.getGiftCard();
            giftCard.setStatus(GiftCardStatus.REDEEMED);
            giftCard.setUpdatedBy(getCurrentUserEmail());
            giftCardRepository.save(giftCard);
            
            log.info("Gift card {} marked as redeemed", giftCard.getId());
        }
        
        redemption.setUpdatedBy(getCurrentUserEmail());
        Redemption updatedRedemption = redemptionRepository.save(redemption);
        
        log.info("Verification status updated to {} for redemption {}", newStatus, request.getRedemptionId());
        
        return RedemptionResponse.fromEntity(updatedRedemption);
    }

    @Override
    public RedemptionResponse completeRedemption(UUID redemptionId) {
        log.info("Completing redemption: {}", redemptionId);
        
        Redemption redemption = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Redemption not found: " + redemptionId));
        
        if (redemption.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new BadRequestException("Cannot complete redemption. Verification status: " + 
                    redemption.getVerificationStatus().getDisplayName());
        }
        
        redemption.setBeneficiarySignatureCaptured(true);
        redemption.setUpdatedBy(getCurrentUserEmail());
        
        Redemption completedRedemption = redemptionRepository.save(redemption);
        log.info("Redemption {} completed", redemptionId);
        
        return RedemptionResponse.fromEntity(completedRedemption);
    }

    @Override
    @Transactional(readOnly = true)
    public RedemptionResponse getRedemptionByGiftCardId(UUID giftCardId) {
        return redemptionRepository.findByGiftCardId(giftCardId)
                .map(RedemptionResponse::fromEntity)
                .orElse(null);
    }

    @Override
    public String generateReferenceNumber() {
        String year = String.valueOf(Year.now().getValue());
        String randomPart = String.format("%05d", (int) (Math.random() * 100000));
        String referenceNumber = "REF-" + year + "-" + randomPart;
        
        // Ensure uniqueness
        while (giftCardRepository.existsByReferenceNumber(referenceNumber)) {
            randomPart = String.format("%05d", (int) (Math.random() * 100000));
            referenceNumber = "REF-" + year + "-" + randomPart;
        }
        
        return referenceNumber;
    }

    // Helper methods
    
    private GiftCard findGiftCardById(UUID id) {
        return giftCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gift card not found: " + id));
    }

    private User getCurrentUser() {
        try {
            return SecurityUtils.getCurrentUserEmail()
                    .flatMap(userRepository::findByEmail)
                    .orElse(null);
        } catch (Exception e) {
            log.debug("Could not get current user: {}", e.getMessage());
            return null;
        }
    }

    private String getCurrentUserEmail() {
        return SecurityUtils.getCurrentUserEmail().orElse("system");
    }

    private String normalizePhone(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^0-9]", "");
    }
}
