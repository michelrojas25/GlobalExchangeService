package com.exchange.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TipoDeCambio {
    private Moneda monedaOrigen;
    private Moneda monedaDestino;
    private BigDecimal tasa;
} 