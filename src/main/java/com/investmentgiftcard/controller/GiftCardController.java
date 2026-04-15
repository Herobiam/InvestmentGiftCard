package com.investmentgiftcard.controller;

import com.investmentgiftcard.dto.request.GiftCardRequest;
import com.investmentgiftcard.dto.request.RedemptionRequest;
import com.investmentgiftcard.dto.request.VerificationRequest;
import com.investmentgiftcard.dto.response.ApiResponse;
import com.investmentgiftcard.dto.response.GiftCardResponse;
import com.investmentgiftcard.dto.response.PagedResponse;
import com.investmentgiftcard.dto.response.RedemptionResponse;
import com.investmentgiftcard.service.GiftCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Gift Card operations
 */
@RestController
@RequestMapping("/api/v1/gift-cards")
@RequiredArgsConstructor
@Tag(name = "Gift Cards", description = "Gift Card management APIs")
public class GiftCardController {

    private final GiftCardService giftCardService;

    @PostMapping
    @Operation(summary = "Create a new gift card")
    public ResponseEntity<ApiResponse<GiftCardResponse>> createGiftCard(
            @Valid @RequestBody GiftCardRequest request) {
        GiftCardResponse response = giftCardService.createGiftCard(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Gift card created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get gift card by ID")
    public ResponseEntity<ApiResponse<GiftCardResponse>> getGiftCardById(@PathVariable UUID id) {
        GiftCardResponse response = giftCardService.getGiftCardById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Gift card retrieved successfully"));
    }

    @GetMapping("/reference/{referenceNumber}")
    @Operation(summary = "Get gift card by reference number")
    public ResponseEntity<ApiResponse<GiftCardResponse>> getGiftCardByReference(
            @PathVariable String referenceNumber) {
        GiftCardResponse response = giftCardService.getGiftCardByReference(referenceNumber);
        return ResponseEntity.ok(ApiResponse.success(response, "Gift card retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all gift cards with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<GiftCardResponse>>> getAllGiftCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<GiftCardResponse> giftCards = giftCardService.getAllGiftCards(pageable);
        PagedResponse<GiftCardResponse> pagedResponse = PagedResponse.from(giftCards);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Gift cards retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search gift cards")
    public ResponseEntity<ApiResponse<PagedResponse<GiftCardResponse>>> searchGiftCards(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GiftCardResponse> giftCards = giftCardService.searchGiftCards(query, pageable);
        
        PagedResponse<GiftCardResponse> pagedResponse = PagedResponse.from(giftCards);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Search results"));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent gift cards")
    public ResponseEntity<ApiResponse<List<GiftCardResponse>>> getRecentGiftCards() {
        List<GiftCardResponse> recentGiftCards = giftCardService.getRecentGiftCards();
        return ResponseEntity.ok(ApiResponse.success(recentGiftCards, "Recent gift cards retrieved"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update gift card status")
    public ResponseEntity<ApiResponse<GiftCardResponse>> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        GiftCardResponse response = giftCardService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Status updated successfully"));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a gift card")
    public ResponseEntity<ApiResponse<GiftCardResponse>> cancelGiftCard(@PathVariable UUID id) {
        GiftCardResponse response = giftCardService.cancelGiftCard(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Gift card cancelled successfully"));
    }

    // Redemption endpoints

    @PostMapping("/redeem")
    @Operation(summary = "Initiate gift card redemption")
    public ResponseEntity<ApiResponse<RedemptionResponse>> initiateRedemption(
            @Valid @RequestBody RedemptionRequest request) {
        RedemptionResponse response = giftCardService.initiateRedemption(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Redemption initiated successfully. Verification call will be placed to buyer."));
    }

    @PostMapping("/redeem/verify")
    @Operation(summary = "Update redemption verification status")
    public ResponseEntity<ApiResponse<RedemptionResponse>> updateVerificationStatus(
            @Valid @RequestBody VerificationRequest request) {
        RedemptionResponse response = giftCardService.updateVerificationStatus(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Verification status updated"));
    }

    @PostMapping("/redeem/{redemptionId}/complete")
    @Operation(summary = "Complete the redemption process")
    public ResponseEntity<ApiResponse<RedemptionResponse>> completeRedemption(
            @PathVariable UUID redemptionId) {
        RedemptionResponse response = giftCardService.completeRedemption(redemptionId);
        return ResponseEntity.ok(ApiResponse.success(response, "Redemption completed successfully"));
    }

    @GetMapping("/{giftCardId}/redemption")
    @Operation(summary = "Get redemption details for a gift card")
    public ResponseEntity<ApiResponse<RedemptionResponse>> getRedemptionByGiftCardId(
            @PathVariable UUID giftCardId) {
        RedemptionResponse response = giftCardService.getRedemptionByGiftCardId(giftCardId);
        if (response == null) {
            return ResponseEntity.ok(ApiResponse.success(null, "No redemption found for this gift card"));
        }
        return ResponseEntity.ok(ApiResponse.success(response, "Redemption details retrieved"));
    }

    @GetMapping("/generate-reference")
    @Operation(summary = "Generate a unique reference number")
    public ResponseEntity<ApiResponse<String>> generateReferenceNumber() {
        String referenceNumber = giftCardService.generateReferenceNumber();
        return ResponseEntity.ok(ApiResponse.success(referenceNumber, "Reference number generated"));
    }
}
