package com.exchange.domain.ports.output;

import com.exchange.domain.model.ExchangeRate;
import java.util.Optional;

public interface ExchangeRateRepository {
    Optional<ExchangeRate> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
    ExchangeRate save(ExchangeRate exchangeRate);
} 