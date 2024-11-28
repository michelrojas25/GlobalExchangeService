package com.exchange.infrastructure.rest.controller;

import com.exchange.domain.model.ExchangeRate;
import com.exchange.domain.ports.input.ExchangeUseCase;
import com.exchange.infrastructure.rest.dto.ExchangeRequestDto;
import com.exchange.infrastructure.rest.dto.ExchangeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {

    private final ExchangeUseCase exchangeUseCase;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API is working!");
    }

    @PostMapping("/calculate")
    public ResponseEntity<ExchangeResponseDto> calculateExchange(@RequestBody ExchangeRequestDto request) {
        var exchangeRate = exchangeUseCase.getExchangeRate(
            request.getFromCurrency(),
            request.getToCurrency()
        );
        
        var exchangedAmount = exchangeUseCase.calculateExchange(
            request.getAmount(),
            request.getFromCurrency(),
            request.getToCurrency()
        );

        var response = ExchangeResponseDto.builder()
            .amount(request.getAmount())
            .exchangedAmount(exchangedAmount)
            .fromCurrency(request.getFromCurrency())
            .toCurrency(request.getToCurrency())
            .rate(exchangeRate.getRate())
            .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/rate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ExchangeRate> updateExchangeRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam BigDecimal rate) {
        
        return ResponseEntity.ok(
            exchangeUseCase.updateExchangeRate(fromCurrency, toCurrency, rate)
        );
    }
} 