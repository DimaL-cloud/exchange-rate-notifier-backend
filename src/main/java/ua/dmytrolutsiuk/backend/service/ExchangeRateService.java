package ua.dmytrolutsiuk.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.dmytrolutsiuk.backend.dto.ExchangeRateResponse;
import ua.dmytrolutsiuk.backend.dto.NbuExchangeRateDto;
import ua.dmytrolutsiuk.backend.model.ExchangeRate;
import ua.dmytrolutsiuk.backend.repository.ExchangeRateRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final NbuApiClient nbuApiClient;

    private static final DateTimeFormatter NBU_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Transactional
    public void fetchAndSaveExchangeRates() {
        log.info("Starting to fetch and save exchange rates");

        List<NbuExchangeRateDto> nbuRates = nbuApiClient.fetchExchangeRates();

        if (nbuRates == null || nbuRates.isEmpty()) {
            log.warn("No exchange rates received from NBU API");
            return;
        }

        int savedCount = 0;
        int updatedCount = 0;

        for (NbuExchangeRateDto nbuRate : nbuRates) {
            try {
                LocalDate exchangeDate = LocalDate.parse(nbuRate.getExchangedate(), NBU_DATE_FORMATTER);

                ExchangeRate exchangeRate = exchangeRateRepository
                        .findByCurrencyCodeAndExchangeDate(nbuRate.getCc(), exchangeDate)
                        .orElse(ExchangeRate.builder()
                                .currencyCode(nbuRate.getCc())
                                .exchangeDate(exchangeDate)
                                .build());

                boolean isNew = exchangeRate.getId() == null;

                exchangeRate.setR030(nbuRate.getR030());
                exchangeRate.setCurrencyName(nbuRate.getTxt());
                exchangeRate.setRate(nbuRate.getRate());

                exchangeRateRepository.save(exchangeRate);

                if (isNew) {
                    savedCount++;
                } else {
                    updatedCount++;
                }
            } catch (Exception e) {
                log.error("Error processing exchange rate for currency: {}", nbuRate.getCc(), e);
            }
        }

        log.info("Exchange rates processing completed. Saved: {}, Updated: {}", savedCount, updatedCount);
    }

    public ExchangeRateResponse getLatestRate(String currencyCode) {
        log.debug("Fetching latest rate for currency: {}", currencyCode);

        ExchangeRate exchangeRate = exchangeRateRepository
                .findFirstByCurrencyCodeOrderByExchangeDateDesc(currencyCode.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Exchange rate not found for currency: " + currencyCode));

        return mapToResponse(exchangeRate);
    }

    public ExchangeRateResponse getRateByDate(String currencyCode, LocalDate date) {
        log.debug("Fetching rate for currency: {} on date: {}", currencyCode, date);

        ExchangeRate exchangeRate = exchangeRateRepository
                .findByCurrencyCodeAndExchangeDate(currencyCode.toUpperCase(), date)
                .orElseThrow(() -> new RuntimeException(
                        "Exchange rate not found for currency: " + currencyCode + " on date: " + date));

        return mapToResponse(exchangeRate);
    }

    private ExchangeRateResponse mapToResponse(ExchangeRate exchangeRate) {
        return ExchangeRateResponse.builder()
                .currencyCode(exchangeRate.getCurrencyCode())
                .currencyName(exchangeRate.getCurrencyName())
                .rate(exchangeRate.getRate())
                .exchangeDate(exchangeRate.getExchangeDate())
                .build();
    }
}
