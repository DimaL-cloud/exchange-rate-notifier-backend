package ua.dmytrolutsiuk.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.dmytrolutsiuk.backend.model.Subscription;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByEmailAndCurrencyCode(String email, String currencyCode);

    List<Subscription> findByActiveTrue();

    List<Subscription> findByCurrencyCodeAndActiveTrue(String currencyCode);
}
