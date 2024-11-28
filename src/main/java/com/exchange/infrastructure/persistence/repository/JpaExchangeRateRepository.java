package com.exchange.infrastructure.persistence.repository;

import com.exchange.infrastructure.persistence.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {
    
    @Query("SELECT e FROM ExchangeRateEntity e WHERE e.fromCurrency = :fromCurrency AND e.toCurrency = :toCurrency ORDER BY e.id DESC LIMIT 1")
    Optional<ExchangeRateEntity> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
} 