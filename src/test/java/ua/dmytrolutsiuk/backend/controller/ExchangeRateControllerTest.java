package ua.dmytrolutsiuk.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.dmytrolutsiuk.backend.dto.ExchangeRateResponse;
import ua.dmytrolutsiuk.backend.exception.GlobalExceptionHandler;
import ua.dmytrolutsiuk.backend.service.ExchangeRateService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ExchangeRateController.class, GlobalExceptionHandler.class})
class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangeRateService exchangeRateService;

    @Test
    void getLatestRate_shouldReturnRate() throws Exception {
        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .currencyCode("USD")
                .currencyName("Долар США")
                .rate(new BigDecimal("42.0423"))
                .exchangeDate(LocalDate.of(2025, 11, 17))
                .build();

        when(exchangeRateService.getLatestRate("USD")).thenReturn(response);

        mockMvc.perform(get("/api/rates/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("USD"))
                .andExpect(jsonPath("$.currencyName").value("Долар США"))
                .andExpect(jsonPath("$.rate").value(42.0423))
                .andExpect(jsonPath("$.exchangeDate").value("2025-11-17"));
    }

    @Test
    void getRateByDate_shouldReturnRate() throws Exception {
        LocalDate date = LocalDate.of(2025, 11, 17);
        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .currencyCode("USD")
                .currencyName("Долар США")
                .rate(new BigDecimal("42.0423"))
                .exchangeDate(date)
                .build();

        when(exchangeRateService.getRateByDate("USD", date)).thenReturn(response);

        mockMvc.perform(get("/api/rates/USD/history")
                        .param("date", "2025-11-17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("USD"))
                .andExpect(jsonPath("$.exchangeDate").value("2025-11-17"));
    }

    @Test
    void getLatestRate_shouldReturn500WhenServiceThrowsException() throws Exception {
        when(exchangeRateService.getLatestRate("INVALID"))
                .thenThrow(new RuntimeException("Currency not found"));

        mockMvc.perform(get("/api/rates/INVALID"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Currency not found"));
    }
}
