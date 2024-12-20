package com.exchange.application.services;

import com.exchange.domain.exception.ExcepcionIntercambioNoEncontrado;
import com.exchange.domain.model.Moneda;
import com.exchange.domain.model.TipoDeCambio;
import com.exchange.domain.model.TendenciaMercado;
import com.exchange.domain.model.VolatilidadMercado;
import com.exchange.domain.model.RecomendacionMercado;
import com.exchange.domain.ports.input.CasoDeUsoDeCambio;
import com.exchange.domain.ports.output.RepositorioTasaDeCambio;
import com.exchange.infrastructure.rest.dto.RespuestaCalculosAvanzadosDto;
import com.exchange.infrastructure.rest.dto.RespuestaCalculosAvanzadosDto.MacdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioCambio implements CasoDeUsoDeCambio {
    
    private static final String MONEDA_BASE = "USD";
    private final RepositorioTasaDeCambio repositorioTasaDeCambio;
    private final ServicioAnalisisTecnico servicioAnalisisTecnico;

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
        BigDecimal spread = tasaBase.getTasa().multiply(new BigDecimal("0.02"));
        
        List<BigDecimal> historicoPreciosRecientes = obtenerHistoricoPreciosRecientes(monedaOrigen, monedaDestino);
        
        Integer rsi = servicioAnalisisTecnico.calcularRSI(historicoPreciosRecientes).intValue();
        TendenciaMercado tendencia = servicioAnalisisTecnico.determinarTendencia(historicoPreciosRecientes);
        MacdDto macd = servicioAnalisisTecnico.calcularMACD(historicoPreciosRecientes);
        VolatilidadMercado volatilidad = servicioAnalisisTecnico.calcularVolatilidad(
            servicioAnalisisTecnico.calcularDesviacionEstandar(historicoPreciosRecientes)
        );
        RecomendacionMercado recomendacion = servicioAnalisisTecnico.determinarRecomendacion(tendencia, rsi, macd);
        
        return RespuestaCalculosAvanzadosDto.builder()
            .par(monedaOrigen + "/" + monedaDestino)
            .fecha(LocalDateTime.now())
            .tasas(RespuestaCalculosAvanzadosDto.TasasDto.builder()
                .compra(tasaBase.getTasa().subtract(spread))
                .venta(tasaBase.getTasa().add(spread))
                .spread(spread)
                .spreadPromedio10Min(spread)
                .spreadPromedio1Hora(spread)
                .build())
            .analisis(RespuestaCalculosAvanzadosDto.AnalisisDto.builder()
                .promedioMovil10Min(tasaBase.getTasa())
                .promedioMovil1Hora(tasaBase.getTasa())
                .maximoUltimos30Dias(tasaBase.getTasa().multiply(new BigDecimal("1.05")))
                .minimoUltimos30Dias(tasaBase.getTasa().multiply(new BigDecimal("0.95")))
                .desviacionEstandar10Min(servicioAnalisisTecnico.calcularDesviacionEstandar(historicoPreciosRecientes))
                .desviacionEstandar1Hora(new BigDecimal("0.1"))
                .tasaCambio10Min(new BigDecimal("-1.02"))
                .tasaCambio1Hora(new BigDecimal("0.85"))
                .tendencia(tendencia.name())
                .volatilidad(volatilidad.name())
                .rsi(rsi)
                .macd(macd)
                .intervaloConfianza95(new double[]{
                    tasaBase.getTasa().doubleValue() - 0.05,
                    tasaBase.getTasa().doubleValue() + 0.05
                })
                .correlacionConUSD_EUR(new BigDecimal("0.85"))
                .recomendacion(recomendacion.name())
                .build())
            .build();
    }

    private List<BigDecimal> obtenerHistoricoPreciosRecientes(String monedaOrigen, String monedaDestino) {
        // TODO: Implementar obtención de histórico desde base de datos
        // Por ahora retornamos datos simulados
        List<BigDecimal> precios = new ArrayList<>();
        BigDecimal tasaBase = obtenerTasaDeCambio(monedaOrigen, monedaDestino).getTasa();
        
        for (int i = 0; i < 30; i++) {
            precios.add(tasaBase.add(new BigDecimal(Math.random() * 0.1 - 0.05)));
        }
        return precios;
    }
} 