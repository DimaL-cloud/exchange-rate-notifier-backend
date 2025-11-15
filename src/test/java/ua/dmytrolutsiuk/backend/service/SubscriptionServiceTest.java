package ua.dmytrolutsiuk.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.dmytrolutsiuk.backend.dto.SubscriptionRequest;
import ua.dmytrolutsiuk.backend.dto.SubscriptionResponse;
import ua.dmytrolutsiuk.backend.model.Subscription;
import ua.dmytrolutsiuk.backend.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private SubscriptionRequest request;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        request = SubscriptionRequest.builder()
                .email("test@example.com")
                .currencyCode("USD")
                .build();

        subscription = Subscription.builder()
                .id(1L)
                .email("test@example.com")
                .currencyCode("USD")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void subscribe_shouldCreateNewSubscription() {
        when(subscriptionRepository.findByEmailAndCurrencyCode("test@example.com", "USD"))
                .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionResponse response = subscriptionService.subscribe(request);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("USD", response.getCurrencyCode());
        assertTrue(response.getActive());
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void subscribe_shouldReactivateInactiveSubscription() {
        subscription.setActive(false);
        when(subscriptionRepository.findByEmailAndCurrencyCode("test@example.com", "USD"))
                .thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionResponse response = subscriptionService.subscribe(request);

        assertNotNull(response);
        assertTrue(response.getActive());
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void subscribe_shouldThrowExceptionWhenAlreadyActive() {
        when(subscriptionRepository.findByEmailAndCurrencyCode("test@example.com", "USD"))
                .thenReturn(Optional.of(subscription));

        assertThrows(RuntimeException.class, () -> subscriptionService.subscribe(request));
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }

    @Test
    void subscribe_shouldNormalizeCurrencyCode() {
        request.setCurrencyCode("usd");
        when(subscriptionRepository.findByEmailAndCurrencyCode("test@example.com", "USD"))
                .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        SubscriptionResponse response = subscriptionService.subscribe(request);

        assertEquals("USD", response.getCurrencyCode());
    }

    @Test
    void unsubscribe_shouldDeactivateSubscription() {
        when(subscriptionRepository.findByEmailAndCurrencyCode("test@example.com", "USD"))
                .thenReturn(Optional.of(subscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(subscription);

        subscriptionService.unsubscribe("test@example.com", "USD");

        verify(subscriptionRepository, times(1)).save(argThat(sub -> !sub.getActive()));
    }

    @Test
    void unsubscribe_shouldThrowExceptionWhenNotFound() {
        when(subscriptionRepository.findByEmailAndCurrencyCode("test@example.com", "USD"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> subscriptionService.unsubscribe("test@example.com", "USD"));
        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }
}
