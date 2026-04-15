package com.investmentgiftcard.repository;

import com.investmentgiftcard.model.entity.GiftCard;
import com.investmentgiftcard.model.entity.Redemption;
import com.investmentgiftcard.model.entity.Redemption.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Redemption entity operations
 */
@Repository
public interface RedemptionRepository extends JpaRepository<Redemption, UUID> {

    /**
     * Find redemption by gift card
     */
    Optional<Redemption> findByGiftCard(GiftCard giftCard);

    /**
     * Find redemption by gift card ID
     */
    Optional<Redemption> findByGiftCardId(UUID giftCardId);

    /**
     * Check if redemption exists for a gift card
     */
    boolean existsByGiftCardId(UUID giftCardId);

    /**
     * Find redemptions by verification status
     */
    List<Redemption> findByVerificationStatusAndIsDeletedFalse(VerificationStatus status);

    /**
     * Find redemptions by verification status with pagination
     */
    Page<Redemption> findByVerificationStatusAndIsDeletedFalse(VerificationStatus status, Pageable pageable);

    /**
     * Find pending verification redemptions
     */
    List<Redemption> findByVerificationStatusInAndIsDeletedFalse(List<VerificationStatus> statuses);

    /**
     * Find all active redemptions
     */
    Page<Redemption> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Count redemptions by verification status
     */
    long countByVerificationStatusAndIsDeletedFalse(VerificationStatus status);
}
