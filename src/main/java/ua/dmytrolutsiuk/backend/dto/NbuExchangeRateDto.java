package ua.dmytrolutsiuk.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NbuExchangeRateDto {
    private Integer r030;
    private String txt;
    private BigDecimal rate;
    private String cc;
    private String exchangedate;
}
