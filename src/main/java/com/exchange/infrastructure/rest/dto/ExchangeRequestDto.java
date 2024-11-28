package com.exchange.infrastructure.rest.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ExchangeRequestDto {
    private BigDecimal amount;
    private String fromCurrency;
    private String toCurrency;
}