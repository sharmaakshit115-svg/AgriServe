package com.agriserve.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI (Swagger) configuration.
 * Defines the API metadata and Bearer-token security scheme.
 * Access Swagger UI at: http://localhost:8080/api/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "AgriServe API",
        version = "1.0.0",
        description = "Agricultural Extension & Farmer Advisory System — RESTful API documentation",
        contact = @Contact(name = "AgriServe Dev Team", email = "dev@agriserve.in"),
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
    ),
    servers = {
        @Server(url = "/api", description = "Default Server"),
        @Server(url = "http://localhost:8080/api", description = "Local Development")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Enter your JWT token in the format: Bearer <token>"
)
public class OpenApiConfig {
    // Configuration is entirely annotation-driven
}
