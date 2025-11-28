package ua.dmytrolutsiuk.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Exchange rate information for a specific currency")
public class ExchangeRateResponse {

    @Schema(description = "ISO 4217 currency code", example = "USD")
    private String currencyCode;

    @Schema(description = "Full name of the currency", example = "US Dollar")
    private String currencyName;

    @Schema(description = "Exchange rate value relative to UAH", example = "41.25")
    private BigDecimal rate;

    @Schema(description = "Date of the exchange rate", example = "2024-01-15")
    private LocalDate exchangeDate;
}
