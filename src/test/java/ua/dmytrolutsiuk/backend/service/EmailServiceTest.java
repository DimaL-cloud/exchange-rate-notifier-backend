package ua.dmytrolutsiuk.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import ua.dmytrolutsiuk.backend.dto.ExchangeRateResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private ExchangeRateResponse exchangeRate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@example.com");

        exchangeRate = ExchangeRateResponse.builder()
                .currencyCode("USD")
                .currencyName("Долар США")
                .rate(new BigDecimal("42.0423"))
                .exchangeDate(LocalDate.of(2025, 11, 17))
                .build();
    }

    @Test
    void sendExchangeRateNotification_shouldSendEmail() {
        String toEmail = "test@example.com";
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendExchangeRateNotification(toEmail, exchangeRate);

        verify(mailSender, times(1)).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertNotNull(sentMessage);
        assertEquals("noreply@example.com", sentMessage.getFrom());
        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals("Exchange Rate Update: USD", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("USD"));
        assertTrue(sentMessage.getText().contains("42.0423"));
        assertTrue(sentMessage.getText().contains("Долар США"));
    }

    @Test
    void sendExchangeRateNotification_shouldHandleExceptionGracefully() {
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendExchangeRateNotification("test@example.com", exchangeRate));
    }
}
