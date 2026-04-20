package co.edu.uniquindio.proyecto.infrastructure.config.setup;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.RolSeguridadEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.List;

/**
 * Propiedades de configuración para los usuarios por defecto del sistema.
 *
 * <p>Lee el prefijo {@code default-users} del {@code application.properties}
 * y mapea cada entrada a un {@link DefaultUser}. Es usada por
 * {@link DefaultUserInitializer} para crear los usuarios iniciales
 * con sus contraseñas encriptadas.</p>
 *
 * <p>Ejemplo de configuración:</p>
 * <pre>
 * default-users.users[0].username=admin@solicitudes.com
 * default-users.users[0].password=admin123
 * default-users.users[0].role=ADMIN
 * </pre>
 */
@Getter
@ConfigurationProperties(prefix = "default-users")
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class DefaultUserProperties {

    private final List<DefaultUser> users;

    /**
     * Representa un usuario por defecto leído desde la configuración.
     *
     * @param username Email del usuario.
     * @param password Contraseña en texto plano (se encripta al persistir).
     * @param role     Rol de seguridad asignado al usuario.
     */
    public record DefaultUser(String username, String password, RolSeguridadEnum role) {}
}