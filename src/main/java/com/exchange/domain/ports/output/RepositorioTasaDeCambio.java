package com.exchange.domain.ports.output;

import com.exchange.domain.model.TipoDeCambio;
import java.util.Optional;

public interface RepositorioTasaDeCambio {
    Optional<TipoDeCambio> findByMonedaOrigenAndMonedaDestino(String monedaOrigen, String monedaDestino);
    TipoDeCambio guardar(TipoDeCambio tasaDeCambio);
    TipoDeCambio actualizar(TipoDeCambio tasaDeCambio);
} 