package ua.dmytrolutsiuk.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.dmytrolutsiuk.backend.dto.SubscriptionRequest;
import ua.dmytrolutsiuk.backend.dto.SubscriptionResponse;
import ua.dmytrolutsiuk.backend.model.Subscription;
import ua.dmytrolutsiuk.backend.repository.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public SubscriptionResponse subscribe(SubscriptionRequest request) {
        log.info("Creating subscription for email: {} and currency: {}",
                request.getEmail(), request.getCurrencyCode());

        String normalizedCurrencyCode = request.getCurrencyCode().toUpperCase();

        Subscription subscription = subscriptionRepository
                .findByEmailAndCurrencyCode(request.getEmail(), normalizedCurrencyCode)
                .orElse(Subscription.builder()
                        .email(request.getEmail())
                        .currencyCode(normalizedCurrencyCode)
                        .build());

        if (subscription.getId() != null && Boolean.TRUE.equals(subscription.getActive())) {
            throw new RuntimeException("Subscription already exists for this email and currency");
        }

        subscription.setActive(true);
        subscription = subscriptionRepository.save(subscription);

        log.info("Subscription created successfully with ID: {}", subscription.getId());

        return mapToResponse(subscription);
    }

    @Transactional
    public void unsubscribe(String email, String currencyCode) {
        log.info("Unsubscribing email: {} from currency: {}", email, currencyCode);

        String normalizedCurrencyCode = currencyCode.toUpperCase();

        Subscription subscription = subscriptionRepository
                .findByEmailAndCurrencyCode(email, normalizedCurrencyCode)
                .orElseThrow(() -> new RuntimeException(
                        "Subscription not found for email: " + email + " and currency: " + currencyCode));

        subscription.setActive(false);
        subscriptionRepository.save(subscription);

        log.info("Subscription deactivated successfully");
    }

    public List<Subscription> getAllActiveSubscriptions() {
        return subscriptionRepository.findByActiveTrue();
    }

    private SubscriptionResponse mapToResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .email(subscription.getEmail())
                .currencyCode(subscription.getCurrencyCode())
                .active(subscription.getActive())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
