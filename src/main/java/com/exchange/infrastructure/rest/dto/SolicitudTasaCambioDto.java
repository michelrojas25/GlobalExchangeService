package com.exchange.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import com.exchange.infrastructure.validation.CodigoMonedaValido;

@Data
@Schema(description = "DTO para actualizar tipo de cambio", 
       example = "{\"monedaOrigen\": \"USD\", \"monedaDestino\": \"PEN\", \"tasa\": 3.8}")
public class SolicitudTasaCambioDto {
    @Schema(description = "Codigo de moneda origen", example = "USD")
    @NotBlank(message = "La moneda de origen es requerida")
    @Size(min = 3, max = 3, message = "El codigo de la moneda debe tener 3 caracteres")
    @CodigoMonedaValido(message = "El codigo de moneda es inválido")
    private String monedaOrigen;

    @Schema(description = "Codigo de moneda destino", example = "PEN")
    @NotBlank(message = "La moneda de destino es requerida")
    @Size(min = 3, max = 3, message = "El codigo de la moneda debe tener 3 caracteres")
    @CodigoMonedaValido(message = "El codigo de moneda es inválido")
    private String monedaDestino;

    @Schema(description = "Tasa de cambio", example = "3.8")
    @NotNull(message = "La tasa es requerida")
    @Positive(message = "La tasa debe ser positiva")
    private BigDecimal tasa;
} 