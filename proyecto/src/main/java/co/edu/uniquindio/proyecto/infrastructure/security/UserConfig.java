package co.edu.uniquindio.proyecto.infrastructure.security;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Conecta Spring Security con la base de datos de usuarios.
 * Le indica al framework dónde buscar las credenciales durante el login.
 */
@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsServiceFromDataBase(UsuarioJpaDataRepository repository) {
        return username -> repository.findByEmail(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + username
                ));
    }
}