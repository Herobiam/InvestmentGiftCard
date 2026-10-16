package com.investmentgiftcard.service.impl;

import com.investmentgiftcard.model.entity.GiftCard;
import com.investmentgiftcard.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Implementation of EmailService for sending email notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@fnb.com.na}")
    private String fromEmail;

    @Value("${app.name:FNB Investment Gift Card}")
    private String appName;

    @Override
    @Async
    public void sendGiftCardNotification(GiftCard giftCard) {
        if (giftCard.getBeneficiaryEmail() == null || giftCard.getBeneficiaryEmail().isBlank()) {
            log.info("No email address for beneficiary of gift card {}, skipping email notification", 
                    giftCard.getReferenceNumber());
            return;
        }

        String subject = "🎁 You've Received an FNB Investment Gift Card!";
        String htmlContent = buildGiftCardEmailTemplate(giftCard);

        try {
            sendHtmlEmail(giftCard.getBeneficiaryEmail(), subject, htmlContent);
            log.info("Gift card notification email sent to {} for card {}", 
                    giftCard.getBeneficiaryEmail(), giftCard.getReferenceNumber());
        } catch (Exception e) {
            log.error("Failed to send gift card notification email to {} for card {}: {}", 
                    giftCard.getBeneficiaryEmail(), giftCard.getReferenceNumber(), e.getMessage());
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.debug("Email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildGiftCardEmailTemplate(GiftCard giftCard) {
        NumberFormat currencyFormat = NumberFormat.getInstance(Locale.US);
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        
        String formattedValue = "N$ " + currencyFormat.format(giftCard.getTotalValue());
        String formattedExpiry = giftCard.getExpiryDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        String beneficiaryName = giftCard.getBeneficiaryFullName();
        String buyerName = giftCard.getBuyerFullName();
        String referenceNumber = giftCard.getReferenceNumber();
        
        // Determine card colors based on value
        // Black: N$10,000 and above
        // Green (Teal): N$5,001 - N$9,999
        // Gold: N$5,000 and below
        BigDecimal value = giftCard.getTotalValue();
        String cardGradient;
        String cardShadow;
        String cardTier;
        
        if (value.compareTo(new BigDecimal("10000")) >= 0) {
            // Black/Platinum tier
            cardGradient = "linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 30%, #1a1a1a 50%, #3d3d3d 70%, #1a1a1a 100%)";
            cardShadow = "0 10px 40px rgba(0,0,0,0.4)";
            cardTier = "PLATINUM";
        } else if (value.compareTo(new BigDecimal("5000")) > 0) {
            // Green/Teal tier
            cardGradient = "linear-gradient(135deg, #007c7f 0%, #00a3a6 30%, #007c7f 50%, #009999 70%, #005a5c 100%)";
            cardShadow = "0 10px 40px rgba(0,124,127,0.3)";
            cardTier = "CLASSIC";
        } else {
            // Gold tier
            cardGradient = "linear-gradient(135deg, #b8860b 0%, #daa520 30%, #b8860b 50%, #ffd700 70%, #b8860b 100%)";
            cardShadow = "0 10px 40px rgba(184,134,11,0.3)";
            cardTier = "GOLD";
        }
        
        // Build HTML without String.formatted() to avoid issues with CSS percentage signs
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>FNB Investment Gift Card</title>");
        html.append("</head>");
        html.append("<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;\">");
        html.append("<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">");
        html.append("<tr>");
        html.append("<td align=\"center\" style=\"padding: 40px 20px;\">");
        html.append("<table role=\"presentation\" style=\"width: 100%; max-width: 600px; border-collapse: collapse;\">");
        
        // Header
        html.append("<tr>");
        html.append("<td style=\"background: linear-gradient(135deg, #007c7f 0%, #005a5c 100%); padding: 30px 40px; border-radius: 16px 16px 0 0;\">");
        html.append("<table role=\"presentation\" style=\"width: 100%;\">");
        html.append("<tr>");
        html.append("<td>");
        html.append("<h1 style=\"margin: 0; color: #ffffff; font-size: 24px; font-weight: 600;\">FNB Namibia</h1>");
        html.append("<p style=\"margin: 5px 0 0 0; color: rgba(255,255,255,0.9); font-size: 14px;\">Investment Gift Card</p>");
        html.append("</td>");
        html.append("<td align=\"right\">");
        html.append("<div style=\"width: 60px; height: 60px; background: rgba(255,255,255,0.2); border-radius: 50%; text-align: center; line-height: 60px;\">");
        html.append("<span style=\"font-size: 30px;\">🎁</span>");
        html.append("</div>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        
        // Gift Card Body
        html.append("<tr>");
        html.append("<td style=\"background: #ffffff; padding: 0;\">");
        
        // Congratulations Message
        html.append("<table role=\"presentation\" style=\"width: 100%; padding: 30px 40px; background: linear-gradient(180deg, #f8fffe 0%, #ffffff 100%);\">");
        html.append("<tr>");
        html.append("<td align=\"center\">");
        html.append("<h2 style=\"margin: 0 0 10px 0; color: #333; font-size: 28px; font-weight: 600;\">Congratulations, ").append(beneficiaryName).append("!</h2>");
        html.append("<p style=\"margin: 0; color: #666; font-size: 16px; line-height: 1.6;\">");
        html.append("You've received an Investment Gift Card from <strong>").append(buyerName).append("</strong>");
        html.append("</p>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        
        // Gift Card Visual - Dynamic color based on value
        html.append("<table role=\"presentation\" style=\"width: calc(100% - 60px); margin: 0 30px 30px 30px; border-collapse: collapse;\">");
        html.append("<tr>");
        html.append("<td style=\"background: ").append(cardGradient).append("; border-radius: 16px; padding: 30px; box-shadow: ").append(cardShadow).append(";\">");
        html.append("<table role=\"presentation\" style=\"width: 100%;\">");
        
        // Top row with FNB logo area and tier badge
        html.append("<tr>");
        html.append("<td>");
        html.append("<table role=\"presentation\" style=\"width: 100%;\">");
        html.append("<tr>");
        html.append("<td>");
        html.append("<p style=\"margin: 0; color: rgba(255,255,255,0.9); font-size: 16px; font-weight: 600;\">FNB</p>");
        html.append("<p style=\"margin: 2px 0 0 0; color: rgba(255,255,255,0.7); font-size: 10px; text-transform: uppercase; letter-spacing: 1px;\">Investment Gift Card</p>");
        html.append("</td>");
        html.append("<td align=\"right\">");
        html.append("<span style=\"background: rgba(255,255,255,0.2); color: #ffffff; padding: 4px 12px; border-radius: 12px; font-size: 10px; font-weight: 600; letter-spacing: 1px;\">").append(cardTier).append("</span>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        
        // Value display
        html.append("<tr>");
        html.append("<td style=\"padding-top: 20px;\">");
        html.append("<p style=\"margin: 0 0 5px 0; color: rgba(255,255,255,0.7); font-size: 11px; text-transform: uppercase; letter-spacing: 1px;\">Gift Card Value</p>");
        html.append("<h3 style=\"margin: 0; color: #ffffff; font-size: 38px; font-weight: 700;\">").append(formattedValue).append("</h3>");
        html.append("</td>");
        html.append("</tr>");
        
        // Reference and expiry
        html.append("<tr>");
        html.append("<td style=\"padding-top: 25px;\">");
        html.append("<table role=\"presentation\" style=\"width: 100%;\">");
        html.append("<tr>");
        html.append("<td style=\"width: 50%;\">");
        html.append("<p style=\"margin: 0 0 3px 0; color: rgba(255,255,255,0.6); font-size: 10px; text-transform: uppercase; letter-spacing: 0.5px;\">Reference No.</p>");
        html.append("<p style=\"margin: 0; color: #ffffff; font-size: 13px; font-weight: 600;\">").append(referenceNumber).append("</p>");
        html.append("</td>");
        html.append("<td style=\"width: 50%;\" align=\"right\">");
        html.append("<p style=\"margin: 0 0 3px 0; color: rgba(255,255,255,0.6); font-size: 10px; text-transform: uppercase; letter-spacing: 0.5px;\">Valid Until</p>");
        html.append("<p style=\"margin: 0; color: #ffffff; font-size: 13px; font-weight: 600;\">").append(formattedExpiry).append("</p>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        
        // Divider
        html.append("<tr>");
        html.append("<td style=\"padding-top: 15px;\">");
        html.append("<div style=\"height: 1px; background: rgba(255,255,255,0.15);\"></div>");
        html.append("</td>");
        html.append("</tr>");
        
        // Beneficiary name
        html.append("<tr>");
        html.append("<td style=\"padding-top: 12px;\">");
        html.append("<table role=\"presentation\" style=\"width: 100%;\">");
        html.append("<tr>");
        html.append("<td>");
        html.append("<p style=\"margin: 0 0 2px 0; color: rgba(255,255,255,0.5); font-size: 9px; text-transform: uppercase; letter-spacing: 0.5px;\">Beneficiary</p>");
        html.append("<p style=\"margin: 0; color: #ffffff; font-size: 14px; font-weight: 500;\">").append(beneficiaryName).append("</p>");
        html.append("</td>");
        html.append("<td align=\"right\" style=\"vertical-align: bottom;\">");
        html.append("<p style=\"margin: 0; color: rgba(255,255,255,0.5); font-size: 9px;\">NAMIBIA</p>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        
        // Instructions
        html.append("<table role=\"presentation\" style=\"width: 100%; padding: 0 40px 30px 40px;\">");
        html.append("<tr>");
        html.append("<td>");
        html.append("<h4 style=\"margin: 0 0 15px 0; color: #333; font-size: 18px; font-weight: 600;\">How to Redeem Your Gift Card</h4>");
        html.append("<table role=\"presentation\" style=\"width: 100%;\">");
        
        // Step 1
        html.append("<tr>");
        html.append("<td style=\"padding: 10px 0; border-bottom: 1px solid #eee;\">");
        html.append("<table role=\"presentation\">");
        html.append("<tr>");
        html.append("<td style=\"width: 36px; vertical-align: top;\">");
        html.append("<div style=\"width: 28px; height: 28px; background: #e8f5f5; border-radius: 50%; text-align: center; line-height: 28px; color: #007c7f; font-weight: 600; font-size: 14px;\">1</div>");
        html.append("</td>");
        html.append("<td style=\"color: #555; font-size: 14px; line-height: 1.5;\">");
        html.append("Visit any FNB Namibia branch with your ID/Passport");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        
        // Step 2
        html.append("<tr>");
        html.append("<td style=\"padding: 10px 0; border-bottom: 1px solid #eee;\">");
        html.append("<table role=\"presentation\">");
        html.append("<tr>");
        html.append("<td style=\"width: 36px; vertical-align: top;\">");
        html.append("<div style=\"width: 28px; height: 28px; background: #e8f5f5; border-radius: 50%; text-align: center; line-height: 28px; color: #007c7f; font-weight: 600; font-size: 14px;\">2</div>");
        html.append("</td>");
        html.append("<td style=\"color: #555; font-size: 14px; line-height: 1.5;\">");
        html.append("Provide your gift card reference number: <strong>").append(referenceNumber).append("</strong>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        
        // Step 3
        html.append("<tr>");
        html.append("<td style=\"padding: 10px 0;\">");
        html.append("<table role=\"presentation\">");
        html.append("<tr>");
        html.append("<td style=\"width: 36px; vertical-align: top;\">");
        html.append("<div style=\"width: 28px; height: 28px; background: #e8f5f5; border-radius: 50%; text-align: center; line-height: 28px; color: #007c7f; font-weight: 600; font-size: 14px;\">3</div>");
        html.append("</td>");
        html.append("<td style=\"color: #555; font-size: 14px; line-height: 1.5;\">");
        html.append("Choose your preferred investment product and complete the redemption");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        
        // Important Notice
        html.append("<table role=\"presentation\" style=\"width: calc(100% - 80px); margin: 0 40px 30px 40px; background: #fff8e6; border-radius: 8px; border-left: 4px solid #f5a623;\">");
        html.append("<tr>");
        html.append("<td style=\"padding: 15px 20px;\">");
        html.append("<p style=\"margin: 0; color: #996300; font-size: 13px; line-height: 1.5;\">");
        html.append("<strong>⚠️ Important:</strong> This gift card expires on <strong>").append(formattedExpiry).append("</strong>. ");
        html.append("Please redeem before this date to enjoy your investment benefits.");
        html.append("</p>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        
        html.append("</td>");
        html.append("</tr>");
        
        // Footer
        html.append("<tr>");
        html.append("<td style=\"background: #f8f9fa; padding: 25px 40px; border-radius: 0 0 16px 16px; border-top: 1px solid #eee;\">");
        html.append("<table role=\"presentation\" style=\"width: 100%;\">");
        html.append("<tr>");
        html.append("<td align=\"center\">");
        html.append("<p style=\"margin: 0 0 10px 0; color: #007c7f; font-size: 14px; font-weight: 600;\">FNB Namibia</p>");
        html.append("<p style=\"margin: 0 0 5px 0; color: #888; font-size: 12px;\">How can we help you?</p>");
        html.append("<p style=\"margin: 0; color: #666; font-size: 12px;\">");
        html.append("📞 +264 61 299 2111 &nbsp;|&nbsp; 🌐 www.fnbnamibia.com.na");
        html.append("</p>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</td>");
        html.append("</tr>");
        
        html.append("</table>");
        
        // Disclaimer
        html.append("<table role=\"presentation\" style=\"width: 100%; max-width: 600px; margin-top: 20px;\">");
        html.append("<tr>");
        html.append("<td align=\"center\">");
        html.append("<p style=\"margin: 0; color: #999; font-size: 11px; line-height: 1.5;\">");
        html.append("This email was sent to you because you are the beneficiary of an FNB Investment Gift Card.<br>");
        html.append("If you believe you received this email in error, please contact our support team.");
        html.append("</p>");
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        
        html.append("</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
}
