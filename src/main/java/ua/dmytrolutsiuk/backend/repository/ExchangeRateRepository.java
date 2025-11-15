package ua.dmytrolutsiuk.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.dmytrolutsiuk.backend.model.ExchangeRate;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    Optional<ExchangeRate> findByCurrencyCodeAndExchangeDate(String currencyCode, LocalDate exchangeDate);

    Optional<ExchangeRate> findFirstByCurrencyCodeOrderByExchangeDateDesc(String currencyCode);
}
