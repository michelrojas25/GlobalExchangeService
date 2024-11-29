package com.exchange.domain.ports.input;

import com.exchange.domain.model.TipoDeCambio;
import java.math.BigDecimal;

public interface CasoDeUsoDeCambio {
    BigDecimal calcularCambio(BigDecimal monto, String monedaOrigen, String monedaDestino);
    TipoDeCambio actualizarTasaDeCambio(String monedaOrigen, String monedaDestino, BigDecimal tasa);
    TipoDeCambio obtenerTasaDeCambio(String monedaOrigen, String monedaDestino);
} 