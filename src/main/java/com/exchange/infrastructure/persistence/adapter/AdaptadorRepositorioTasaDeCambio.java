package com.exchange.infrastructure.persistence.adapter;

import com.exchange.domain.model.Moneda;
import com.exchange.domain.model.TipoDeCambio;
import com.exchange.domain.ports.output.RepositorioTasaDeCambio;
import com.exchange.infrastructure.persistence.entity.EntidadTasaDeCambio;
import com.exchange.infrastructure.persistence.repository.JpaRepositorioTasaDeCambio;
import com.exchange.domain.exception.ExcepcionIntercambioNoEncontrado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdaptadorRepositorioTasaDeCambio implements RepositorioTasaDeCambio {

    private final JpaRepositorioTasaDeCambio jpaRepositorio;

    @Override
    public Optional<TipoDeCambio> findByMonedaOrigenAndMonedaDestino(String monedaOrigen, String monedaDestino) {
        return jpaRepositorio.findByMonedaOrigenAndMonedaDestino(monedaOrigen, monedaDestino)
            .map(entity -> new TipoDeCambio(
                new Moneda(entity.getMonedaOrigen(), entity.getMonedaOrigen()),
                new Moneda(entity.getMonedaDestino(), entity.getMonedaDestino()),
                entity.getTasa()
            ));
    }

    @Override
    public TipoDeCambio guardar(TipoDeCambio tipoDeCambio) {
        var entidad = new EntidadTasaDeCambio();
        entidad.setMonedaOrigen(tipoDeCambio.getMonedaOrigen().getCodigo());
        entidad.setMonedaDestino(tipoDeCambio.getMonedaDestino().getCodigo());
        entidad.setTasa(tipoDeCambio.getTasa());
        
        var savedEntity = jpaRepositorio.save(entidad);
        return new TipoDeCambio(
            new Moneda(savedEntity.getMonedaOrigen(), savedEntity.getMonedaOrigen()),
            new Moneda(savedEntity.getMonedaDestino(), savedEntity.getMonedaDestino()),
            savedEntity.getTasa()
        );
    }

    @Override
    public TipoDeCambio actualizar(TipoDeCambio tipoDeCambio) {
        EntidadTasaDeCambio entidadExistente = jpaRepositorio
            .findByMonedaOrigenAndMonedaDestino(
                tipoDeCambio.getMonedaOrigen().getCodigo(),
                tipoDeCambio.getMonedaDestino().getCodigo()
            )
            .orElseThrow(() -> new ExcepcionIntercambioNoEncontrado("Tipo de cambio no encontrado"));

        entidadExistente.setTasa(tipoDeCambio.getTasa());
        EntidadTasaDeCambio entidadGuardada = jpaRepositorio.save(entidadExistente);
        
        return new TipoDeCambio(
            new Moneda(entidadGuardada.getMonedaOrigen(), entidadGuardada.getMonedaOrigen()),
            new Moneda(entidadGuardada.getMonedaDestino(), entidadGuardada.getMonedaDestino()),
            entidadGuardada.getTasa()
        );
    }
} 