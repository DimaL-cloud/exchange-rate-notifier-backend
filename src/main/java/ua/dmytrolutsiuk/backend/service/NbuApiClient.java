package ua.dmytrolutsiuk.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ua.dmytrolutsiuk.backend.dto.NbuExchangeRateDto;

import java.util.List;

@Service
@Slf4j
public class NbuApiClient {

    private final RestClient restClient;
    private final String nbuApiUrl;

    public NbuApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${nbu.api.url}") String nbuApiUrl) {
        this.restClient = restClientBuilder.build();
        this.nbuApiUrl = nbuApiUrl;
    }

    public List<NbuExchangeRateDto> fetchExchangeRates() {
        log.info("Fetching exchange rates from NBU API: {}", nbuApiUrl);

        try {
            List<NbuExchangeRateDto> rates = restClient.get()
                    .uri(nbuApiUrl)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            log.info("Successfully fetched {} exchange rates", rates != null ? rates.size() : 0);
            return rates;
        } catch (Exception e) {
            log.error("Error fetching exchange rates from NBU API", e);
            throw new RuntimeException("Failed to fetch exchange rates from NBU API", e);
        }
    }
}
