package com.exchange.infrastructure.rest.dto;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class RespuestaErrorValidacion extends ErrorResponse {
    private final Map<String, String> errors;

    public RespuestaErrorValidacion(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
        super(status, message, timestamp);
        this.errors = errors;
    }
}