package com.exchange.application.services;

import com.exchange.domain.exception.ExcepcionIntercambioNoEncontrado;
import com.exchange.domain.model.Moneda;
import com.exchange.domain.model.TipoDeCambio;
import com.exchange.domain.ports.input.CasoDeUsoDeCambio;
import com.exchange.domain.ports.output.RepositorioTasaDeCambio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ServicioCambio implements CasoDeUsoDeCambio {
    
    private final RepositorioTasaDeCambio repositorioTasaDeCambio;

    @Override
    public BigDecimal calcularCambio(BigDecimal monto, String monedaOrigen, String monedaDestino) {
        TipoDeCambio tasa = repositorioTasaDeCambio
            .findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
            .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                String.format("Tipo de cambio no encontrado para %s a %s", monedaOrigen, monedaDestino)
            ));
            
        return monto.multiply(tasa.getTasa()).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public TipoDeCambio actualizarTasaDeCambio(String monedaOrigen, String monedaDestino, BigDecimal tasa) {
        // Primero buscar si existe
        TipoDeCambio existingRate = repositorioTasaDeCambio
            .findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
            .orElse(null);
        
        Moneda from = new Moneda(monedaOrigen, monedaOrigen);
        Moneda to = new Moneda(monedaDestino, monedaDestino);
        
        if (existingRate != null) {
            // Actualizar el existente
            return repositorioTasaDeCambio.actualizar(new TipoDeCambio(from, to, tasa));
        } else {
            // Crear uno nuevo
            return repositorioTasaDeCambio.guardar(new TipoDeCambio(from, to, tasa));
        }
    }

    @Override
    public TipoDeCambio obtenerTasaDeCambio(String monedaOrigen, String monedaDestino) {
        return repositorioTasaDeCambio
            .findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
            .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado(
                String.format("Tasa de intercambio no encontrada para %s to %s", monedaOrigen, monedaDestino)
            ));
    }
} 