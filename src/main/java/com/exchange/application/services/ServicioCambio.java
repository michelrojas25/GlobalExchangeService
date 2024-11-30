package com.exchange.application.services;

import com.exchange.domain.exception.ExcepcionIntercambioNoEncontrado;
import com.exchange.domain.model.Moneda;
import com.exchange.domain.model.TipoDeCambio;
import com.exchange.domain.ports.input.CasoDeUsoDeCambio;
import com.exchange.domain.ports.output.RepositorioTasaDeCambio;
import com.exchange.infrastructure.rest.dto.RespuestaCalculosAvanzadosDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicioCambio implements CasoDeUsoDeCambio {
    
    private static final String MONEDA_BASE = "USD";
    private final RepositorioTasaDeCambio repositorioTasaDeCambio;

    @Override
    public BigDecimal calcularCambio(BigDecimal monto, String monedaOrigen, String monedaDestino) {
        // Si las monedas son iguales
        if (monedaOrigen.equals(monedaDestino)) {
            return monto;
        }

        // Si la moneda origen es USD (caso directo)
        if (monedaOrigen.equals(MONEDA_BASE)) {
            TipoDeCambio tasaDirecta = repositorioTasaDeCambio
                .findByMonedaOrigenAndMonedaDestino(MONEDA_BASE, monedaDestino)
                .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                    String.format("No se encontró tasa para %s a %s", MONEDA_BASE, monedaDestino)));
            
            return monto.multiply(tasaDirecta.getTasa())
                .setScale(2, RoundingMode.HALF_UP);
        }

        // Si la moneda destino es USD (caso inverso)
        if (monedaDestino.equals(MONEDA_BASE)) {
            TipoDeCambio tasaDirecta = repositorioTasaDeCambio
                .findByMonedaOrigenAndMonedaDestino(MONEDA_BASE, monedaOrigen)
                .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                    String.format("No se encontró tasa para %s a %s", MONEDA_BASE, monedaOrigen)));
            
            return monto.divide(tasaDirecta.getTasa(), 6, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
        }

        // Conversión a través de USD
        TipoDeCambio tasaOrigenUSD = repositorioTasaDeCambio
            .findByMonedaOrigenAndMonedaDestino(MONEDA_BASE, monedaOrigen)
            .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                String.format("No se encontró tasa para %s a %s", MONEDA_BASE, monedaOrigen)));
        
        TipoDeCambio tasaUSDDestino = repositorioTasaDeCambio
            .findByMonedaOrigenAndMonedaDestino(MONEDA_BASE, monedaDestino)
            .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                String.format("No se encontró tasa para %s a %s", MONEDA_BASE, monedaDestino)));
        
        return monto
            .divide(tasaOrigenUSD.getTasa(), 6, RoundingMode.HALF_UP)
            .multiply(tasaUSDDestino.getTasa())
            .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public TipoDeCambio actualizarTasaDeCambio(String monedaOrigen, String monedaDestino, BigDecimal tasa) {
        // Solo permitir actualizaciones donde USD es la moneda origen
        if (!monedaOrigen.equals(MONEDA_BASE)) {
            throw new IllegalArgumentException(
                "Solo se permiten actualizaciones de tasas donde USD es la moneda origen"
            );
        }
        
        Moneda from = new Moneda(monedaOrigen, monedaOrigen);
        Moneda to = new Moneda(monedaDestino, monedaDestino);
        
        // Buscar si existe la tasa
        TipoDeCambio existingRate = repositorioTasaDeCambio
            .findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
            .orElse(null);
        
        if (existingRate != null) {
            return repositorioTasaDeCambio.actualizar(new TipoDeCambio(from, to, tasa));
        } else {
            return repositorioTasaDeCambio.guardar(new TipoDeCambio(from, to, tasa));
        }
    }

    @Override
    public TipoDeCambio obtenerTasaDeCambio(String monedaOrigen, String monedaDestino) {
        // Si las monedas son iguales
        if (monedaOrigen.equals(monedaDestino)) {
            return new TipoDeCambio(
                new Moneda(monedaOrigen, monedaOrigen),
                new Moneda(monedaDestino, monedaDestino),
                BigDecimal.ONE
            );
        }

        // Si la moneda origen es USD (caso directo)
        if (monedaOrigen.equals(MONEDA_BASE)) {
            return repositorioTasaDeCambio
                .findByMonedaOrigenAndMonedaDestino(MONEDA_BASE, monedaDestino)
                .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                    String.format("No se encontró tasa para %s a %s", MONEDA_BASE, monedaDestino)
                ));
        }

        // Si la moneda destino es USD (caso inverso)
        if (monedaDestino.equals(MONEDA_BASE)) {
            TipoDeCambio tasaDirecta = repositorioTasaDeCambio
                .findByMonedaOrigenAndMonedaDestino(MONEDA_BASE, monedaOrigen)
                .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                    String.format("No se encontró tasa para %s a %s", MONEDA_BASE, monedaOrigen)
                ));
            
            return new TipoDeCambio(
                new Moneda(monedaOrigen, monedaOrigen),
                new Moneda(monedaDestino, monedaDestino),
                BigDecimal.ONE.divide(tasaDirecta.getTasa(), 6, RoundingMode.HALF_UP)
            );
        }

        // Conversión a través de USD
        TipoDeCambio tasaOrigenUSD = repositorioTasaDeCambio
            .findByMonedaOrigenAndMonedaDestino(MONEDA_BASE, monedaOrigen)
            .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                String.format("No se encontró tasa para %s a %s", MONEDA_BASE, monedaOrigen)
            ));
        
        TipoDeCambio tasaUSDDestino = repositorioTasaDeCambio
            .findByMonedaOrigenAndMonedaDestino(MONEDA_BASE, monedaDestino)
            .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                String.format("No se encontró tasa para %s a %s", MONEDA_BASE, monedaDestino)
            ));
        
        BigDecimal tasaCalculada = tasaUSDDestino.getTasa()
            .divide(tasaOrigenUSD.getTasa(), 6, RoundingMode.HALF_UP);
        
        return new TipoDeCambio(
            new Moneda(monedaOrigen, monedaOrigen),
            new Moneda(monedaDestino, monedaDestino),
            tasaCalculada
        );
    }

    @Override
    public RespuestaCalculosAvanzadosDto obtenerCalculosAvanzados(String monedaOrigen, String monedaDestino) {
        TipoDeCambio tasaBase = obtenerTasaDeCambio(monedaOrigen, monedaDestino);
        BigDecimal spread = tasaBase.getTasa().multiply(new BigDecimal("0.02")); // 2% spread
        
        return RespuestaCalculosAvanzadosDto.builder()
            .par(monedaOrigen + "/" + monedaDestino)
            .fecha(LocalDateTime.now())
            .tasas(RespuestaCalculosAvanzadosDto.TasasDto.builder()
                .compra(tasaBase.getTasa().subtract(spread))
                .venta(tasaBase.getTasa().add(spread))
                .spread(spread)
                .build())
            .analisis(RespuestaCalculosAvanzadosDto.AnalisisDto.builder()
                .promedioMovil7Dias(tasaBase.getTasa())
                .promedioMovil30Dias(tasaBase.getTasa())
                .tendencia("ESTABLE")
                .volatilidad("BAJA")
                .build())
            .build();
    }
} 