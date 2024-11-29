package com.exchange.domain.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum CodigoMondeda {
    USD, EUR, PEN, COP, MXN, ARS, CLP, BRL;

    private static final Set<String> CODIGO_VALIDO = new HashSet<>(Arrays.asList(
        "USD", "EUR", "PEN", "COP", "MXN", "ARS", "CLP", "BRL"
    ));

    public static boolean isValid(String code) {
        return CODIGO_VALIDO.contains(code);
    }
} 