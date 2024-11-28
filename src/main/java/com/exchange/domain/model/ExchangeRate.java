package com.exchange.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {
    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal rate;
} 