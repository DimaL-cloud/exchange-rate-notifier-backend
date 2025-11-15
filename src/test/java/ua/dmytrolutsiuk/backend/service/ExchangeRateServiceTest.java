package ua.dmytrolutsiuk.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.dmytrolutsiuk.backend.dto.ExchangeRateResponse;
import ua.dmytrolutsiuk.backend.dto.NbuExchangeRateDto;
import ua.dmytrolutsiuk.backend.model.ExchangeRate;
import ua.dmytrolutsiuk.backend.repository.ExchangeRateRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private NbuApiClient nbuApiClient;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    private NbuExchangeRateDto nbuDto;
    private ExchangeRate exchangeRate;

    @BeforeEach
    void setUp() {
        nbuDto = NbuExchangeRateDto.builder()
                .r030(840)
                .txt("Долар США")
                .rate(new BigDecimal("42.0423"))
                .cc("USD")
                .exchangedate("17.11.2025")
                .build();

        exchangeRate = ExchangeRate.builder()
                .id(1L)
                .r030(840)
                .currencyCode("USD")
                .currencyName("Долар США")
                .rate(new BigDecimal("42.0423"))
                .exchangeDate(LocalDate.of(2025, 11, 17))
                .build();
    }

    @Test
    void fetchAndSaveExchangeRates_shouldSaveNewRates() {
        List<NbuExchangeRateDto> nbuRates = Arrays.asList(nbuDto);
        when(nbuApiClient.fetchExchangeRates()).thenReturn(nbuRates);
        when(exchangeRateRepository.findByCurrencyCodeAndExchangeDate(any(), any()))
                .thenReturn(Optional.empty());
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenReturn(exchangeRate);

        exchangeRateService.fetchAndSaveExchangeRates();

        verify(nbuApiClient, times(1)).fetchExchangeRates();
        verify(exchangeRateRepository, times(1)).save(any(ExchangeRate.class));
    }

    @Test
    void fetchAndSaveExchangeRates_shouldUpdateExistingRates() {
        List<NbuExchangeRateDto> nbuRates = Arrays.asList(nbuDto);
        when(nbuApiClient.fetchExchangeRates()).thenReturn(nbuRates);
        when(exchangeRateRepository.findByCurrencyCodeAndExchangeDate(any(), any()))
                .thenReturn(Optional.of(exchangeRate));
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenReturn(exchangeRate);

        exchangeRateService.fetchAndSaveExchangeRates();

        verify(exchangeRateRepository, times(1)).save(any(ExchangeRate.class));
    }

    @Test
    void getLatestRate_shouldReturnLatestRate() {
        when(exchangeRateRepository.findFirstByCurrencyCodeOrderByExchangeDateDesc("USD"))
                .thenReturn(Optional.of(exchangeRate));

        ExchangeRateResponse response = exchangeRateService.getLatestRate("USD");

        assertNotNull(response);
        assertEquals("USD", response.getCurrencyCode());
        assertEquals("Долар США", response.getCurrencyName());
        assertEquals(new BigDecimal("42.0423"), response.getRate());
        assertEquals(LocalDate.of(2025, 11, 17), response.getExchangeDate());
    }

    @Test
    void getLatestRate_shouldThrowExceptionWhenNotFound() {
        when(exchangeRateRepository.findFirstByCurrencyCodeOrderByExchangeDateDesc("USD"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> exchangeRateService.getLatestRate("USD"));
    }

    @Test
    void getRateByDate_shouldReturnRateForSpecificDate() {
        LocalDate date = LocalDate.of(2025, 11, 17);
        when(exchangeRateRepository.findByCurrencyCodeAndExchangeDate("USD", date))
                .thenReturn(Optional.of(exchangeRate));

        ExchangeRateResponse response = exchangeRateService.getRateByDate("USD", date);

        assertNotNull(response);
        assertEquals("USD", response.getCurrencyCode());
        assertEquals(date, response.getExchangeDate());
    }

    @Test
    void getRateByDate_shouldThrowExceptionWhenNotFound() {
        LocalDate date = LocalDate.of(2025, 11, 17);
        when(exchangeRateRepository.findByCurrencyCodeAndExchangeDate("USD", date))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> exchangeRateService.getRateByDate("USD", date));
    }
}
