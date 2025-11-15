package ua.dmytrolutsiuk.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.dmytrolutsiuk.backend.dto.SubscriptionRequest;
import ua.dmytrolutsiuk.backend.dto.SubscriptionResponse;
import ua.dmytrolutsiuk.backend.service.SubscriptionService;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(
            @Valid @RequestBody SubscriptionRequest request
    ) {
        SubscriptionResponse response = subscriptionService.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(
            @RequestParam String email,
            @RequestParam String currencyCode
    ) {
        subscriptionService.unsubscribe(email, currencyCode);
        return ResponseEntity.noContent().build();
    }
}
