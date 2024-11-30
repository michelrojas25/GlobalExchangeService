package com.exchange.infrastructure.rest.controller;

import com.exchange.domain.model.TipoDeCambio;
import com.exchange.domain.model.CodigoMondeda;
import com.exchange.domain.ports.input.CasoDeUsoDeCambio;
import com.exchange.infrastructure.rest.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
@Validated
@Tag(name = "Exchange", description = "API de operaciones de tipo de cambio")
public class ControladorDeCambio {

    private final CasoDeUsoDeCambio casoDeUsoDeCambio;

    @Operation(summary = "Verificar estado del API")
    @ApiResponse(responseCode = "200", description = "API funcionando correctamente")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("¡API funcionando correctamente!");
    }

    @Operation(summary = "Calcular tipo de cambio")
    @ApiResponse(responseCode = "200", description = "Cálculo exitoso")
    @ApiResponse(responseCode = "404", description = "Tipo de cambio no encontrado")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @PostMapping("/calculate")
    public ResponseEntity<RespuestaCambioDto> calcularCambio(@Valid @RequestBody SolicitudCambioDto request) {
        var tasaDeCambio = casoDeUsoDeCambio.obtenerTasaDeCambio(
            request.getMonedaOrigen(),
            request.getMonedaDestino()
        );
        
        var montoConvertido = casoDeUsoDeCambio.calcularCambio(
            request.getMonto(),
            request.getMonedaOrigen(),
            request.getMonedaDestino()
        );

        var respuesta = RespuestaCambioDto.builder()
            .monto(request.getMonto())
            .montoConvertido(montoConvertido)
            .monedaOrigen(request.getMonedaOrigen())
            .monedaDestino(request.getMonedaDestino())
            .tasa(tasaDeCambio.getTasa())
            .build();

        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Actualizar tipo de cambio")
    @ApiResponse(responseCode = "200", description = "Actualización exitosa")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Tipo de cambio no encontrado")
    @PostMapping(value = "/tasa", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actualizarTasaDeCambio(
            @Valid @RequestBody SolicitudTasaCambioDto request
    ) {
        try {
            // Validación adicional para códigos de moneda
            if (!CodigoMondeda.isValid(request.getMonedaOrigen()) || 
                !CodigoMondeda.isValid(request.getMonedaDestino())) {
                return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Codigo de moneda invalido",
                        LocalDateTime.now()
                    ));
            }

            // Validación para tasa
            if (request.getTasa() == null || request.getTasa().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "La tasa de cambio debe ser mayor que cero",
                        LocalDateTime.now()
                    ));
            }

            TipoDeCambio result = casoDeUsoDeCambio.actualizarTasaDeCambio(
                request.getMonedaOrigen(),
                request.getMonedaDestino(),
                request.getTasa()
            );
            
            return ResponseEntity.ok(RespuestaTasaCambio.builder()
                .monedaOrigen(result.getMonedaOrigen().getCodigo())
                .monedaDestino(result.getMonedaDestino().getCodigo())
                .tasa(result.getTasa())
                .message("Tipo de cambio actualizado correctamente")
                .build());
                
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Error al actualizar tipo de cambio: " + e.getMessage(),
                    LocalDateTime.now()
                ));
        }
    }

    @Operation(summary = "Obtener cálculos avanzados de tipo de cambio")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Cálculo exitoso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RespuestaCalculosAvanzadosDto.class)
            )
        )
    })
    @GetMapping("/analytics/{monedaOrigen}/{monedaDestino}")
    public ResponseEntity<RespuestaCalculosAvanzadosDto> obtenerCalculosAvanzados(
        @Parameter(description = "Código de moneda origen", example = "USD")
        @PathVariable String monedaOrigen,
        @Parameter(description = "Código de moneda destino", example = "PEN")
        @PathVariable String monedaDestino
    ) {
        if (!CodigoMondeda.isValid(monedaOrigen) || !CodigoMondeda.isValid(monedaDestino)) {
            throw new IllegalArgumentException("Código de moneda inválido");
        }
        
        var resultado = casoDeUsoDeCambio.obtenerCalculosAvanzados(monedaOrigen, monedaDestino);
        return ResponseEntity.ok(resultado);
    }
} 