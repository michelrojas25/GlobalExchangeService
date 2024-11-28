package com.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "exchange_rates")
@Data
public class ExchangeRateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "from_currency")
    private String fromCurrency;
    
    @Column(name = "to_currency")
    private String toCurrency;
    
    private BigDecimal rate;
} 