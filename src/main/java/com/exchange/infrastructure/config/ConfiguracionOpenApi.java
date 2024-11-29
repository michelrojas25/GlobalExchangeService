package com.exchange.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionOpenApi {
    
    @Bean
    public OpenAPI tipoDeCambioOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Tipo de Cambio")
                .description("API para el calculo y gestion de tipos de cambio")
                .version("1.0.0"));
    }
}
