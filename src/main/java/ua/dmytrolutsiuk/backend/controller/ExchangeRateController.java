package ua.dmytrolutsiuk.backend.controller;

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
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/{currencyCode}")
    public ResponseEntity<ExchangeRateResponse> getLatestRate(
            @PathVariable String currencyCode) {
        ExchangeRateResponse rate = exchangeRateService.getLatestRate(currencyCode);
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/{currencyCode}/history")
    public ResponseEntity<ExchangeRateResponse> getRateByDate(
            @PathVariable String currencyCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ExchangeRateResponse rate = exchangeRateService.getRateByDate(currencyCode, date);
        return ResponseEntity.ok(rate);
    }
}
