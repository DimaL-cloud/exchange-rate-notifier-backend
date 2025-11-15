package ua.dmytrolutsiuk.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.dmytrolutsiuk.backend.dto.ExchangeRateResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendExchangeRateNotification(String toEmail, ExchangeRateResponse exchangeRate) {
        log.info("Sending exchange rate notification to: {}", toEmail);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Exchange Rate Update: " + exchangeRate.getCurrencyCode());
            message.setText(buildEmailBody(exchangeRate));

            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", toEmail, e);
        }
    }

    private String buildEmailBody(ExchangeRateResponse exchangeRate) {
        return String.format("""
                Hello,

                Here is the latest exchange rate information:

                Currency: %s (%s)
                Rate: %s UAH
                Date: %s

                Best regards,
                Exchange Rate Notifier
                """,
                exchangeRate.getCurrencyName(),
                exchangeRate.getCurrencyCode(),
                exchangeRate.getRate(),
                exchangeRate.getExchangeDate());
    }
}
