package com.exchange.infrastructure.persistence.adapter;

import com.exchange.domain.model.Currency;
import com.exchange.domain.model.ExchangeRate;
import com.exchange.domain.ports.output.ExchangeRateRepository;
import com.exchange.infrastructure.persistence.entity.ExchangeRateEntity;
import com.exchange.infrastructure.persistence.repository.JpaExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExchangeRateRepositoryAdapter implements ExchangeRateRepository {

    private final JpaExchangeRateRepository jpaRepository;

    @Override
    public Optional<ExchangeRate> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency) {
        return jpaRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
            .map(entity -> new ExchangeRate(
                new Currency(entity.getFromCurrency(), entity.getFromCurrency()),
                new Currency(entity.getToCurrency(), entity.getToCurrency()),
                entity.getRate()
            ));
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        var entity = new ExchangeRateEntity();
        entity.setFromCurrency(exchangeRate.getFromCurrency().getCode());
        entity.setToCurrency(exchangeRate.getToCurrency().getCode());
        entity.setRate(exchangeRate.getRate());
        
        var savedEntity = jpaRepository.save(entity);
        return new ExchangeRate(
            new Currency(savedEntity.getFromCurrency(), savedEntity.getFromCurrency()),
            new Currency(savedEntity.getToCurrency(), savedEntity.getToCurrency()),
            savedEntity.getRate()
        );
    }
} 