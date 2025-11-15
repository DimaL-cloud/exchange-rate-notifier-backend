package ua.dmytrolutsiuk.backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.dmytrolutsiuk.backend.dto.ExchangeRateResponse;
import ua.dmytrolutsiuk.backend.model.Subscription;
import ua.dmytrolutsiuk.backend.service.EmailService;
import ua.dmytrolutsiuk.backend.service.ExchangeRateService;
import ua.dmytrolutsiuk.backend.service.SubscriptionService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateScheduler {

    private final ExchangeRateService exchangeRateService;
    private final SubscriptionService subscriptionService;
    private final EmailService emailService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartup() {
        log.info("Application started - fetching exchange rates");
        fetchRatesAndNotifySubscribers();
    }

    @Scheduled(cron = "${scheduler.exchange-rate.cron}")
    public void scheduledFetchAndNotify() {
        log.info("Scheduled task triggered - fetching exchange rates");
        fetchRatesAndNotifySubscribers();
    }

    private void fetchRatesAndNotifySubscribers() {
        try {
            exchangeRateService.fetchAndSaveExchangeRates();

            sendNotifications();
        } catch (Exception e) {
            log.error("Error in scheduled task", e);
        }
    }

    private void sendNotifications() {
        log.info("Starting to send notifications to subscribers");

        List<Subscription> activeSubscriptions = subscriptionService.getAllActiveSubscriptions();

        if (activeSubscriptions.isEmpty()) {
            log.info("No active subscriptions found");
            return;
        }

        Map<String, List<Subscription>> subscriptionsByCurrency = activeSubscriptions.stream()
                .collect(Collectors.groupingBy(Subscription::getCurrencyCode));

        int successCount = 0;
        int failureCount = 0;

        for (Map.Entry<String, List<Subscription>> entry : subscriptionsByCurrency.entrySet()) {
            String currencyCode = entry.getKey();
            List<Subscription> subscriptions = entry.getValue();

            try {
                ExchangeRateResponse rate = exchangeRateService.getLatestRate(currencyCode);

                for (Subscription subscription : subscriptions) {
                    try {
                        emailService.sendExchangeRateNotification(subscription.getEmail(), rate);
                        successCount++;
                    } catch (Exception e) {
                        log.error("Failed to send notification to: {}", subscription.getEmail(), e);
                        failureCount++;
                    }
                }
            } catch (Exception e) {
                log.error("Failed to get exchange rate for currency: {}", currencyCode, e);
                failureCount += subscriptions.size();
            }
        }

        log.info("Notifications sent. Success: {}, Failures: {}", successCount, failureCount);
    }
}
