package com.example.webfluxdemo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Reactive Sprint 1 API",
                version = "1.0",
                description = "API documentation for the Reactive Sprint 1 application"
        )
)
public class SwaggerConfig {
}
