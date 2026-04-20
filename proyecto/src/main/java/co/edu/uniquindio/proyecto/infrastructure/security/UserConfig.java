package co.edu.uniquindio.proyecto.infrastructure.security;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Configuración que integra Spring Security con la base de datos
 * de usuarios del sistema.
 *
 * <p>Define un {@link UserDetailsService} personalizado que permite
 * a Spring Security obtener las credenciales desde la base de datos.</p>
 */
@Configuration
public class UserConfig {

    /**
     * Implementación de {@link UserDetailsService} que consulta
     * los usuarios desde la base de datos.
     *
     * @param repository Repositorio JPA de usuarios.
     * @return Servicio de detalles de usuario.
     */
    @Bean
    public UserDetailsService userDetailsServiceFromDataBase(UsuarioJpaDataRepository repository) {
        return username -> repository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + username
                ));
    }
}