package co.edu.uniquindio.proyecto.infrastructure.security;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador entre UsuarioEntity y el contrato UserDetails de Spring Security.
 * Spring Security no conoce UsuarioEntity directamente — solo trabaja con UserDetails.
 */
public record CustomUserDetails(UsuarioEntity user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRolSeguridad().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}