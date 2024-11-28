package com.exchange.infrastructure.rest.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ExchangeResponseDto {
    private BigDecimal amount;
    private BigDecimal exchangedAmount;
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal rate;
}