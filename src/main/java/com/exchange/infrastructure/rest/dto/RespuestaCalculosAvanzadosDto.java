package com.exchange.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
        @Schema(description = "Tasa de compra", example = "3.675")
        private BigDecimal compra;
        
        @Schema(description = "Tasa de venta", example = "3.825")
        private BigDecimal venta;
        
        @Schema(description = "Spread entre compra y venta", example = "0.075")
        private BigDecimal spread;
    }
    
    @Data
    @Builder
    public static class AnalisisDto {
        @Schema(description = "Promedio móvil de 7 días", example = "3.75")
        private BigDecimal promedioMovil7Dias;
        
        @Schema(description = "Promedio móvil de 30 días", example = "3.75")
        private BigDecimal promedioMovil30Dias;
        
        @Schema(description = "Tendencia del tipo de cambio", example = "ESTABLE")
        private String tendencia;
        
        @Schema(description = "Nivel de volatilidad", example = "BAJA")
        private String volatilidad;
    }
} 