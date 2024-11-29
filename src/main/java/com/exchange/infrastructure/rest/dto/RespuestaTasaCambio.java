package com.exchange.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Respuesta exitosa de actualización de tipo de cambio")
public class RespuestaTasaCambio {
    @Schema(description = "Moneda origen", example = "USD")
    private String monedaOrigen;
    
    @Schema(description = "Moneda destino", example = "PEN")
    private String monedaDestino;
    
    @Schema(description = "Nueva tasa de cambio", example = "3.8")
    private BigDecimal tasa;
    
    @Schema(description = "Mensaje de exito", example = "Tipo de cambio actualizado correctamente")
    private String message;
}
