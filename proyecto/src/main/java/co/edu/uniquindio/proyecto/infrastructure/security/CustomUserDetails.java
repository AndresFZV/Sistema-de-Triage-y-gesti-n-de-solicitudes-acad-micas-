package co.edu.uniquindio.proyecto.infrastructure.security;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementación personalizada de {@link UserDetails} para integrar
 * la entidad {@link UsuarioEntity} con Spring Security.
 *
 * <p>Adapta la información del usuario persistido a la estructura
 * requerida por el framework de seguridad, proporcionando credenciales
 * y autoridades.</p>
 *
 * <p>Las autoridades incluyen tanto el rol de seguridad como el tipo
 * de usuario definido en el dominio.</p>
 */
public record CustomUserDetails(UsuarioEntity user) implements UserDetails {

    /**
     * Retorna las autoridades (roles/permisos) del usuario.
     *
     * <p>Incluye:</p>
     * <ul>
     *     <li>Rol de seguridad</li>
     *     <li>Tipo de usuario</li>
     * </ul>
     *
     * @return Colección de autoridades.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(user.getRolSeguridad().name()),
                new SimpleGrantedAuthority(user.getTipoUsuario().name())
        );
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña almacenada.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Obtiene el username del usuario.
     *
     * <p>En este caso se utiliza el email como identificador único.</p>
     *
     * @return Email del usuario.
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }
}