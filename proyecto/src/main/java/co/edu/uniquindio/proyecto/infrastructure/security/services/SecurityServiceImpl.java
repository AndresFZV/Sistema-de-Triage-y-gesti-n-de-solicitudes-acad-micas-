package co.edu.uniquindio.proyecto.infrastructure.security.services;

import co.edu.uniquindio.proyecto.application.service.SecurityService;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.LoginRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.RefreshTokenRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.response.TokenResponse;
import co.edu.uniquindio.proyecto.infrastructure.security.JwtTokenProvider;
import co.edu.uniquindio.proyecto.infrastructure.security.TokenBlacklist;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Implementación del servicio de seguridad encargado de la autenticación
 * y gestión de tokens JWT.
 *
 * <p>Se integra con Spring Security para autenticar usuarios y generar
 * tokens de acceso y refresco. Además, gestiona la invalidación de tokens
 * mediante una lista negra.</p>
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Autenticación de usuarios</li>
 *     <li>Generación de access token y refresh token</li>
 *     <li>Renovación de tokens</li>
 *     <li>Invalidación de tokens (logout)</li>
 * </ul>
 */
@Service("securityService")
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklist tokenBlacklist;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.expiry}")
    private long expiry;

    @Value("${jwt.refresh-expiry}")
    private long refreshExpiry;

    /**
     * Autentica un usuario y genera tokens JWT.
     *
     * <p>Valida las credenciales mediante el {@link AuthenticationManager}
     * y genera un access token y un refresh token con sus respectivas fechas
     * de expiración.</p>
     *
     * @param request DTO con credenciales de acceso.
     * @return {@link TokenResponse} con los tokens generados.
     */
    @Override
    public TokenResponse login(LoginRequest request) {
        final var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        final var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        final var now = Instant.now();
        final var expire = now.plus(expiry, ChronoUnit.MINUTES);
        final var refreshExpire = now.plus(refreshExpiry, ChronoUnit.MINUTES);

        String accessToken = jwtTokenProvider.generateTokenAsString(
                authentication.getName(), roles, now, expire
        );
        String refreshToken = jwtTokenProvider.generateTokenAsString(
                authentication.getName(), List.of("REFRESH"), now, refreshExpire
        );

        return new TokenResponse(accessToken, refreshToken, "Bearer", expire, roles);
    }

    /**
     * Renueva un access token a partir de un refresh token válido.
     *
     * <p>Valida que el refresh token no esté invalidado, lo decodifica,
     * extrae la información del usuario y genera nuevos tokens.</p>
     *
     * <p>El refresh token utilizado es invalidado para evitar reutilización.</p>
     *
     * @param request DTO con el refresh token.
     * @return {@link TokenResponse} con los nuevos tokens.
     */
    @Override
    public TokenResponse refresh(RefreshTokenRequest request) {
        if (tokenBlacklist.estaInvalidado(request.refreshToken())) {
            throw new RuntimeException("El refresh token ha sido invalidado");
        }

        var jwt = jwtDecoder.decode(request.refreshToken());
        var roles = jwt.getClaimAsStringList("roles");
        var username = jwt.getSubject();

        var now = Instant.now();
        var expire = now.plus(expiry, ChronoUnit.MINUTES);
        var refreshExpire = now.plus(refreshExpiry, ChronoUnit.MINUTES);

        String accessToken = jwtTokenProvider.generateTokenAsString(
                username, roles, now, expire
        );
        String newRefreshToken = jwtTokenProvider.generateTokenAsString(
                username, List.of("REFRESH"), now, refreshExpire
        );

        tokenBlacklist.invalidar(request.refreshToken());

        return new TokenResponse(accessToken, newRefreshToken, "Bearer", expire, roles);
    }

    /**
     * Cierra la sesión del usuario invalidando el token.
     *
     * <p>El token es agregado a una lista negra para evitar su reutilización.</p>
     *
     * @param token Token de acceso a invalidar.
     */
    @Override
    public void logout(String token) {
        tokenBlacklist.invalidar(token);
    }
}