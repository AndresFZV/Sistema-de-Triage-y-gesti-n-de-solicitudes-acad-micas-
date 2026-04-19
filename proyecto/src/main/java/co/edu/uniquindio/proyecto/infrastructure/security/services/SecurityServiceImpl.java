package co.edu.uniquindio.proyecto.infrastructure.security.services;

import co.edu.uniquindio.proyecto.application.service.SecurityService;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.LoginRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.TokenResponse;
import co.edu.uniquindio.proyecto.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Implementación del servicio de seguridad.
 * Vive en infraestructura porque usa AuthenticationManager y JwtTokenProvider,
 * que son componentes de infraestructura.
 */
@Service("securityService")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiry}")
    private long expiry;

    @Override
    public TokenResponse login(LoginRequest request) {
        // Dispara la autenticación contra la base de datos (configurado en UserConfig)
        final var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        // Si no lanza excepción, el login es correcto
        final var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        final var now = Instant.now();
        final var expire = now.plus(expiry, ChronoUnit.MINUTES);

        return new TokenResponse(
                jwtTokenProvider.generateTokenAsString(authentication.getName(), roles, now, expire),
                "Bearer",
                expire,
                roles
        );
    }
}