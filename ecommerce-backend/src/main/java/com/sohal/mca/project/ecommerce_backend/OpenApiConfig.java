package com.sohal.mca.project.ecommerce_backend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "MCA Project Ecommerce Backend",
        version = "1.0",
        description = "API documentation for the MCA Project Ecommerce Backend"
    )
)
public class OpenApiConfig {
    // This class can be empty or used for other OpenAPI configurations
}