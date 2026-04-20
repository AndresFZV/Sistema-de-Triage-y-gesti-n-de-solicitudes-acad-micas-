package co.edu.uniquindio.proyecto.infrastructure.config.setup;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializador de usuarios por defecto al arrancar la aplicación.
 *
 * <p>Se ejecuta automáticamente al inicio gracias a {@link CommandLineRunner}.
 * Solo inserta los usuarios si la tabla está completamente vacía, evitando
 * duplicados en reinicios. Las contraseñas se encriptan con BCrypt antes
 * de persistirse — nunca se almacenan en texto plano.</p>
 *
 * <p>Los usuarios se configuran en {@code application.properties} bajo
 * el prefijo {@code default-users} y son mapeados por {@link DefaultUserProperties}.</p>
 */
@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements CommandLineRunner {

    private final DefaultUserProperties props;
    private final UsuarioJpaDataRepository repository;
    private final PasswordEncoder encoder;

    /**
     * Inserta los usuarios por defecto si la tabla de usuarios está vacía.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            props.getUsers().forEach(defaultUser -> {
                UsuarioEntity entity = new UsuarioEntity();
                entity.setEmail(defaultUser.username());
                entity.setPassword(encoder.encode(defaultUser.password()));
                entity.setRolSeguridad(defaultUser.role());
                entity.setNombre(defaultUser.username());
                entity.setCodigoExterno(java.util.UUID.randomUUID().toString());
                entity.setTipoUsuario(
                        defaultUser.role().name().equals("ADMIN")
                                ? co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.TipoUsuarioEnum.ADMINISTRATIVO
                                : co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.TipoUsuarioEnum.ESTUDIANTE
                );
                repository.save(entity);
            });
            System.out.println("✅ Security Seed Data finalizado");
        }
    }
}