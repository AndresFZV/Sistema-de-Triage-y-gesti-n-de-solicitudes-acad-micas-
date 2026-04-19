package co.edu.uniquindio.proyecto.infrastructure.config.setup;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

/**
 * Mapea las propiedades default-users del application.properties
 * para crear usuarios iniciales en la base de datos.
 */
@Getter
@ConfigurationProperties(prefix = "default-users")
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class DefaultUserProperties {

    private final List<DefaultUser> users;

    public record DefaultUser(String username, String password, RolSeguridadEnum role) {}
}