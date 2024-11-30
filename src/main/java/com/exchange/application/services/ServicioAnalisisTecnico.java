package com.exchange.application.services;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioAnalisisTecnico {
    
    public BigDecimal calcularRSI(List<BigDecimal> precios) {
        // Implementación básica de RSI
        return new BigDecimal("55");
    }
    
    public BigDecimal calcularDesviacionEstandar(List<BigDecimal> precios) {
        return new BigDecimal("0.05").setScale(4, RoundingMode.HALF_UP);
    }
    
    public double[] calcularIntervaloConfianza(BigDecimal precio) {
        double precioBase = precio.doubleValue();
        return new double[]{precioBase - 0.05, precioBase + 0.05};
    }
    
    public String determinarTendencia(BigDecimal actual, BigDecimal anterior) {
        if (actual.compareTo(anterior) > 0) {
            return "ALCISTA_MODERADA";
        } else if (actual.compareTo(anterior) < 0) {
            return "BAJISTA_MODERADA";
        }
        return "ESTABLE";
    }
    
    public String determinarRecomendacion(String tendencia, Integer rsi) {
        if (rsi > 70) return "VENDER";
        if (rsi < 30) return "COMPRAR";
        return "MANTENER";
    }
} 