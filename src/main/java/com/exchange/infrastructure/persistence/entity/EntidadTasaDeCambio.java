package com.exchange.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "TiposDeCambio")
@Getter
@Setter
public class EntidadTasaDeCambio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "moneda_origen")
    private String monedaOrigen;
    
    @Column(name = "moneda_destino")
    private String monedaDestino;
    
    @Column(precision = 10, scale = 4)
    private BigDecimal tasa;
} 