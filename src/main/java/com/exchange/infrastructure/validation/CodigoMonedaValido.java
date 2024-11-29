package com.exchange.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidadorCodigoMoneda.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodigoMonedaValido {
    String message() default "Codigo de moneda no valido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 