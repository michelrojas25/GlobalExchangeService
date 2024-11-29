package com.exchange.infrastructure.rest.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Respuesta de cálculo de tipo de cambio")
public class RespuestaCambioDto {
    @Schema(description = "Monto original", example = "100.00")
    private BigDecimal monto;
    
    @Schema(description = "Monto convertido", example = "375.00")
    private BigDecimal montoConvertido;
    
    @Schema(description = "Moneda de origen", example = "USD")
    private String monedaOrigen;
    
    @Schema(description = "Moneda de destino", example = "PEN")
    private String monedaDestino;
    
    @Schema(description = "Tasa de cambio aplicada", example = "3.75")
    private BigDecimal tasa;
}