package co.edu.uniquindio.proyecto.infrastructure.config.setup;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inserta usuarios por defecto al iniciar la aplicación si la tabla está vacía.
 * Las contraseñas se encriptan con BCrypt antes de persistirse.
 */
@Component
@RequiredArgsConstructor
public class DefaultUserInitializer implements CommandLineRunner {

    private final DefaultUserProperties props;
    private final UsuarioJpaDataRepository repository;
    private final PasswordEncoder encoder;

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