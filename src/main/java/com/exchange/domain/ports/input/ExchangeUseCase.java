package com.exchange.domain.ports.input;

import com.exchange.domain.model.ExchangeRate;
import java.math.BigDecimal;

public interface ExchangeUseCase {
    BigDecimal calculateExchange(BigDecimal amount, String fromCurrency, String toCurrency);
    ExchangeRate updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate);
    ExchangeRate getExchangeRate(String fromCurrency, String toCurrency);
} 