package com.example.dynamicDto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Geração Dinâmica de DTOs")
                        .description("Cria DTOs com ByteBuddy a partir de JSON e armazena em H2")
                        .version("1.0.0"));
    }
}