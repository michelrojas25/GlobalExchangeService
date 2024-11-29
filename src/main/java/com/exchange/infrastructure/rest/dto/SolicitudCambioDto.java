package com.exchange.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Schema(description = "DTO para solicitud de cambio")
public class SolicitudCambioDto {
    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;

    @NotBlank(message = "La moneda de origen es requerida")
    @Size(min = 3, max = 3, message = "El codigo de moneda debe tener 3 caracteres")
    private String monedaOrigen;

    @NotBlank(message = "La moneda de destino es requerida")
    @Size(min = 3, max = 3, message = "El codigo de moneda debe tener 3 caracteres")
    private String monedaDestino;
}
