package com.investmentgiftcard.repository;

import com.investmentgiftcard.model.entity.GiftCard;
import com.investmentgiftcard.model.entity.GiftCard.GiftCardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for GiftCard entity operations
 */
@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, UUID> {

    /**
     * Find gift card by reference number
     */
    Optional<GiftCard> findByReferenceNumber(String referenceNumber);

    /**
     * Find gift card by batch number
     */
    Optional<GiftCard> findByBatchNumber(String batchNumber);

    /**
     * Check if reference number exists
     */
    boolean existsByReferenceNumber(String referenceNumber);

    /**
     * Check if batch number exists
     */
    boolean existsByBatchNumber(String batchNumber);

    /**
     * Find gift cards by status
     */
    List<GiftCard> findByStatusAndIsDeletedFalse(GiftCardStatus status);

    /**
     * Find gift cards by status with pagination
     */
    Page<GiftCard> findByStatusAndIsDeletedFalse(GiftCardStatus status, Pageable pageable);

    /**
     * Search gift cards by reference, buyer name, or beneficiary name
     */
    @Query("SELECT g FROM GiftCard g WHERE g.isDeleted = false AND " +
            "(LOWER(g.referenceNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(g.buyerFullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(g.beneficiaryFullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<GiftCard> searchGiftCards(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find gift cards by buyer mobile number
     */
    List<GiftCard> findByBuyerMobileAndIsDeletedFalse(String buyerMobile);

    /**
     * Find gift cards by beneficiary mobile number
     */
    List<GiftCard> findByBeneficiaryMobileAndIsDeletedFalse(String beneficiaryMobile);

    /**
     * Find expired gift cards that haven't been marked as expired
     */
    @Query("SELECT g FROM GiftCard g WHERE g.isDeleted = false " +
            "AND g.expiryDate < :today AND g.status != 'EXPIRED' AND g.status != 'REDEEMED'")
    List<GiftCard> findExpiredGiftCards(@Param("today") LocalDate today);

    /**
     * Find all active gift cards
     */
    Page<GiftCard> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find recent gift cards limit
     */
    List<GiftCard> findTop10ByIsDeletedFalseOrderByCreatedAtDesc();

    /**
     * Count gift cards by status
     */
    long countByStatusAndIsDeletedFalse(GiftCardStatus status);
}
