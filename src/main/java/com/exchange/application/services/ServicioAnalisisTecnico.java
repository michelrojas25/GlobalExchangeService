package com.exchange.application.services;

import com.exchange.domain.model.TendenciaMercado;
import com.exchange.domain.model.VolatilidadMercado;
import com.exchange.domain.model.RecomendacionMercado;
import com.exchange.domain.model.Constantes;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.exchange.infrastructure.rest.dto.RespuestaCalculosAvanzadosDto.MacdDto;

@Service
@RequiredArgsConstructor
public class ServicioAnalisisTecnico {
    
    public BigDecimal calcularRSI(List<BigDecimal> precios) {
        if (precios == null || precios.size() < Constantes.PERIODO_RSI) {
            return new BigDecimal("50");
        }
        
        BigDecimal ganancias = BigDecimal.ZERO;
        BigDecimal perdidas = BigDecimal.ZERO;
        
        for (int i = 1; i < precios.size(); i++) {
            BigDecimal diferencia = precios.get(i).subtract(precios.get(i-1));
            if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
                ganancias = ganancias.add(diferencia);
            } else {
                perdidas = perdidas.add(diferencia.abs());
            }
        }
        
        if (perdidas.equals(BigDecimal.ZERO)) {
            return new BigDecimal("100");
        }
        
        BigDecimal rs = ganancias.divide(perdidas, 4, RoundingMode.HALF_UP);
        return new BigDecimal("100").subtract(
            new BigDecimal("100").divide(BigDecimal.ONE.add(rs), 2, RoundingMode.HALF_UP)
        );
    }
    
    public BigDecimal calcularDesviacionEstandar(List<BigDecimal> precios) {
        if (precios == null || precios.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal media = calcularMedia(precios);
        BigDecimal sumaCuadrados = calcularSumaCuadradosDiferencias(precios, media);
        
        return new BigDecimal(Math.sqrt(
            sumaCuadrados.divide(new BigDecimal(precios.size()), 6, RoundingMode.HALF_UP)
            .doubleValue()
        )).setScale(4, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calcularMedia(List<BigDecimal> precios) {
        return precios.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(new BigDecimal(precios.size()), 6, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calcularSumaCuadradosDiferencias(List<BigDecimal> precios, BigDecimal media) {
        return precios.stream()
            .map(precio -> precio.subtract(media).pow(2))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public VolatilidadMercado calcularVolatilidad(BigDecimal desviacionEstandar) {
        if (desviacionEstandar.compareTo(Constantes.VOLATILIDAD_MUY_ALTA) > 0) 
            return VolatilidadMercado.MUY_ALTA;
        if (desviacionEstandar.compareTo(Constantes.VOLATILIDAD_ALTA) > 0) 
            return VolatilidadMercado.ALTA;
        if (desviacionEstandar.compareTo(Constantes.VOLATILIDAD_MODERADA) > 0) 
            return VolatilidadMercado.MODERADA;
        if (desviacionEstandar.compareTo(Constantes.VOLATILIDAD_BAJA) > 0) 
            return VolatilidadMercado.BAJA;
        return VolatilidadMercado.MUY_BAJA;
    }
    
    public TendenciaMercado determinarTendencia(List<BigDecimal> precios) {
        if (precios == null || precios.size() < 2) {
            return TendenciaMercado.ESTABLE;
        }
        
        BigDecimal diferencia = precios.get(precios.size() - 1)
            .subtract(precios.get(precios.size() - 2))
            .divide(precios.get(precios.size() - 2), 4, RoundingMode.HALF_UP);

        if (diferencia.compareTo(Constantes.TENDENCIA_FUERTE) > 0) 
            return TendenciaMercado.FUERTEMENTE_ALCISTA;
        if (diferencia.compareTo(Constantes.TENDENCIA_MODERADA) > 0) 
            return TendenciaMercado.ALCISTA_MODERADA;
        if (diferencia.compareTo(Constantes.TENDENCIA_MODERADA.negate()) < 0) 
            return TendenciaMercado.BAJISTA_MODERADA;
        if (diferencia.compareTo(Constantes.TENDENCIA_FUERTE.negate()) < 0) 
            return TendenciaMercado.FUERTEMENTE_BAJISTA;
        return TendenciaMercado.ESTABLE;
    }
    
    public RecomendacionMercado determinarRecomendacion(TendenciaMercado tendencia, Integer rsi, MacdDto macd) {
        if (rsi > Constantes.RSI_SOBRECOMPRA && macd.getHistograma().compareTo(BigDecimal.ZERO) < 0) {
            return RecomendacionMercado.VENDER;
        }
        if (rsi < Constantes.RSI_SOBREVENTA && macd.getHistograma().compareTo(BigDecimal.ZERO) > 0) {
            return RecomendacionMercado.COMPRAR;
        }
        if (tendencia == TendenciaMercado.FUERTEMENTE_ALCISTA && rsi < Constantes.RSI_NEUTRAL_ALTO) {
            return RecomendacionMercado.COMPRAR;
        }
        if (tendencia == TendenciaMercado.FUERTEMENTE_BAJISTA && rsi > Constantes.RSI_NEUTRAL_BAJO) {
            return RecomendacionMercado.VENDER;
        }
        return RecomendacionMercado.MANTENER;
    }
    
    public MacdDto calcularMACD(List<BigDecimal> precios) {
        if (precios == null || precios.size() < 26) {
            return MacdDto.builder()
                .lineaRapida(BigDecimal.ZERO)
                .lineaLenta(BigDecimal.ZERO)
                .histograma(BigDecimal.ZERO)
                .build();
        }

        BigDecimal ema12 = calcularEMA(precios, 12);
        BigDecimal ema26 = calcularEMA(precios, 26);
        BigDecimal macdLine = ema12.subtract(ema26);
        BigDecimal signalLine = calcularEMA(List.of(macdLine), 9);
        
        return MacdDto.builder()
            .lineaRapida(macdLine)
            .lineaLenta(signalLine)
            .histograma(macdLine.subtract(signalLine))
            .build();
    }
    
    private BigDecimal calcularEMA(List<BigDecimal> precios, int periodo) {
        if (precios == null || precios.size() < periodo) {
            return BigDecimal.ZERO;
        }

        // Calcular SMA inicial
        BigDecimal sma = precios.subList(0, periodo).stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(new BigDecimal(periodo), 8, RoundingMode.HALF_UP);

        BigDecimal multiplicador = new BigDecimal(2)
            .divide(new BigDecimal(periodo + 1), 8, RoundingMode.HALF_UP);
        
        BigDecimal ema = sma;
        for (int i = periodo; i < precios.size(); i++) {
            ema = precios.get(i).multiply(multiplicador)
                .add(ema.multiply(BigDecimal.ONE.subtract(multiplicador)));
        }
        return ema.setScale(6, RoundingMode.HALF_UP);
    }
} 