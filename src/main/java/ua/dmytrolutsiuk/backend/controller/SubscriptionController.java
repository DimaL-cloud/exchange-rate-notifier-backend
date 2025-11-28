package ua.dmytrolutsiuk.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Subscriptions", description = "API for managing email subscriptions to currency rate notifications")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "Subscribe to currency rate notifications",
            description = "Creates a new email subscription for daily notifications about a specific currency exchange rate"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Subscription created successfully",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data (invalid email format or currency code)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Subscription already exists",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(
            @RequestBody(description = "Subscription details including email and currency code", required = true)
            @Valid @org.springframework.web.bind.annotation.RequestBody SubscriptionRequest request
    ) {
        SubscriptionResponse response = subscriptionService.subscribe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Unsubscribe from currency rate notifications",
            description = "Removes an existing email subscription for a specific currency exchange rate"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Subscription removed successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Subscription not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(
            @Parameter(description = "Email address of the subscriber", example = "user@example.com", required = true)
            @RequestParam String email,
            @Parameter(description = "ISO 4217 currency code", example = "USD", required = true)
            @RequestParam String currencyCode
    ) {
        subscriptionService.unsubscribe(email, currencyCode);
        return ResponseEntity.noContent().build();
    }
}
