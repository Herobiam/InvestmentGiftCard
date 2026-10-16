package com.investmentgiftcard.service;

import com.investmentgiftcard.model.entity.GiftCard;

/**
 * Service for sending email notifications
 */
public interface EmailService {
    
    /**
     * Send gift card notification email to beneficiary
     * @param giftCard the gift card to notify about
     */
    void sendGiftCardNotification(GiftCard giftCard);
    
    /**
     * Send a simple email
     * @param to recipient email
     * @param subject email subject
     * @param htmlContent HTML content of the email
     */
    void sendHtmlEmail(String to, String subject, String htmlContent);
}
