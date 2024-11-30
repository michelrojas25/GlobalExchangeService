package com.exchange.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Respuesta de cálculos avanzados de tipo de cambio")
public class RespuestaCalculosAvanzadosDto {
    @Schema(description = "Par de monedas", example = "USD/PEN")
    private String par;
    
    @Schema(description = "Fecha de cálculo")
    private LocalDateTime fecha;
    
    @Schema(description = "Tasas de compra/venta")
    private TasasDto tasas;
    
    @Schema(description = "Análisis de tendencias")
    private AnalisisDto analisis;
    
    @Data
    @Builder
    public static class TasasDto {
        private BigDecimal compra;
        private BigDecimal venta;
        private BigDecimal spread;
        private BigDecimal spreadPromedio10Min;
        private BigDecimal spreadPromedio1Hora;
    }
    
    @Data
    @Builder
    public static class AnalisisDto {
        private BigDecimal promedioMovil10Min;
        private BigDecimal promedioMovil1Hora;
        private BigDecimal maximoUltimos30Dias;
        private BigDecimal minimoUltimos30Dias;
        private BigDecimal desviacionEstandar10Min;
        private BigDecimal desviacionEstandar1Hora;
        private BigDecimal tasaCambio10Min;
        private BigDecimal tasaCambio1Hora;
        private String tendencia;
        private String volatilidad;
        private Integer rsi;
        private MacdDto macd;
        private double[] intervaloConfianza95;
        private BigDecimal correlacionConUSD_EUR;
        private String recomendacion;
    }
    
    @Data
    @Builder
    public static class MacdDto {
        private BigDecimal lineaRapida;
        private BigDecimal lineaLenta;
        private BigDecimal histograma;
    }
} 