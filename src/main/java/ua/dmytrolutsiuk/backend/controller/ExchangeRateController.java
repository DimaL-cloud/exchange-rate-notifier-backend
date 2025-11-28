package ua.dmytrolutsiuk.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.dmytrolutsiuk.backend.dto.ExchangeRateResponse;
import ua.dmytrolutsiuk.backend.service.ExchangeRateService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
@Tag(name = "Exchange Rates", description = "API for retrieving currency exchange rates from NBU")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @Operation(
            summary = "Get latest exchange rate",
            description = "Retrieves the most recent exchange rate for a specified currency code from the National Bank of Ukraine"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved exchange rate",
                    content = @Content(schema = @Schema(implementation = ExchangeRateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Currency not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @GetMapping("/{currencyCode}")
    public ResponseEntity<ExchangeRateResponse> getLatestRate(
            @Parameter(description = "ISO 4217 currency code (e.g., USD, EUR, GBP)", example = "USD", required = true)
            @PathVariable String currencyCode) {
        ExchangeRateResponse rate = exchangeRateService.getLatestRate(currencyCode);
        return ResponseEntity.ok(rate);
    }

    @Operation(
            summary = "Get historical exchange rate",
            description = "Retrieves the exchange rate for a specified currency code on a specific date from the National Bank of Ukraine"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved exchange rate",
                    content = @Content(schema = @Schema(implementation = ExchangeRateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Currency or date not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date format",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content
            )
    })
    @GetMapping("/{currencyCode}/history")
    public ResponseEntity<ExchangeRateResponse> getRateByDate(
            @Parameter(description = "ISO 4217 currency code (e.g., USD, EUR, GBP)", example = "USD", required = true)
            @PathVariable String currencyCode,
            @Parameter(description = "Date for historical rate in ISO format", example = "2024-01-15", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ExchangeRateResponse rate = exchangeRateService.getRateByDate(currencyCode, date);
        return ResponseEntity.ok(rate);
    }
}
