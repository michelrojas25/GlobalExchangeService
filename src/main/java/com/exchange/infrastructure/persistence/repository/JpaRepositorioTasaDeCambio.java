package com.exchange.infrastructure.persistence.repository;

import com.exchange.infrastructure.persistence.entity.EntidadTasaDeCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaRepositorioTasaDeCambio extends JpaRepository<EntidadTasaDeCambio, Long> {
    Optional<EntidadTasaDeCambio> findByMonedaOrigenAndMonedaDestino(String monedaOrigen, String monedaDestino);
} 