package co.edu.uniquindio.proyecto.infrastructure.config.setup;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI con Swagger UI.
 *
 * <p>Define los metadatos de la API y el esquema de seguridad JWT
 * que aparecerá en la interfaz de Swagger. Al agregar
 * {@code @SecurityRequirement}, todos los endpoints muestran
 * el botón "Authorize" para enviar el token Bearer en las
 * peticiones de prueba.</p>
 *
 * <p>La interfaz está disponible en:
 * {@code http://localhost:8080/swagger-ui.html}</p>
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Sistema de Triage Académico",
                version = "1.0",
                description = "API REST para la gestión del ciclo de vida de solicitudes académicas"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}