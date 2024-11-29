package com.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.exchange")
public class AplicacionCambio {
    public static void main(String[] args) {
        SpringApplication.run(AplicacionCambio.class, args);
    }
} 