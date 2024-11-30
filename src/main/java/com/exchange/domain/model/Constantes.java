package com.exchange.domain.model;

import java.math.BigDecimal;

public class Constantes {
    // Umbrales de volatilidad
    public static final BigDecimal VOLATILIDAD_MUY_ALTA = new BigDecimal("0.02");
    public static final BigDecimal VOLATILIDAD_ALTA = new BigDecimal("0.015");
    public static final BigDecimal VOLATILIDAD_MODERADA = new BigDecimal("0.01");
    public static final BigDecimal VOLATILIDAD_BAJA = new BigDecimal("0.005");
    
    // Umbrales de tendencia
    public static final BigDecimal TENDENCIA_FUERTE = new BigDecimal("0.02");
    public static final BigDecimal TENDENCIA_MODERADA = new BigDecimal("0.01");
    
    // Umbrales RSI
    public static final int RSI_SOBREVENTA = 30;
    public static final int RSI_SOBRECOMPRA = 70;
    public static final int RSI_NEUTRAL_BAJO = 40;
    public static final int RSI_NEUTRAL_ALTO = 60;
    
    // Periodos
    public static final int PERIODO_RSI = 14;
    public static final int PERIODO_EMA_RAPIDA = 12;
    public static final int PERIODO_EMA_LENTA = 26;
    public static final int PERIODO_SIGNAL = 9;
} 