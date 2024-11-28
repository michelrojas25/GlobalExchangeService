package com.exchange.application.services;

import com.exchange.domain.exception.ExchangeNotFoundException;
import com.exchange.domain.model.Currency;
import com.exchange.domain.model.ExchangeRate;
import com.exchange.domain.ports.input.ExchangeUseCase;
import com.exchange.domain.ports.output.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ExchangeService implements ExchangeUseCase {
    
    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    public BigDecimal calculateExchange(BigDecimal amount, String fromCurrency, String toCurrency) {
        ExchangeRate rate = exchangeRateRepository
            .findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
            .orElseThrow(() -> new ExchangeNotFoundException(
                String.format("Exchange rate not found for %s to %s", fromCurrency, toCurrency)
            ));
            
        return amount.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public ExchangeRate updateExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate) {
        Currency from = new Currency(fromCurrency, fromCurrency);
        Currency to = new Currency(toCurrency, toCurrency);
        return exchangeRateRepository.save(new ExchangeRate(from, to, rate));
    }

    @Override
    public ExchangeRate getExchangeRate(String fromCurrency, String toCurrency) {
        return exchangeRateRepository
            .findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
            .orElseThrow(() -> new ExchangeNotFoundException(
                String.format("Exchange rate not found for %s to %s", fromCurrency, toCurrency)
            ));
    }
} 