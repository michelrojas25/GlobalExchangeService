package com.exchange.infrastructure.validation;

import com.exchange.domain.model.CodigoMondeda;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidadorCodigoMoneda implements ConstraintValidator<CodigoMonedaValido, String> {
    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null) {
            return false;
        }
        return CodigoMondeda.isValid(valor);
    }
} 