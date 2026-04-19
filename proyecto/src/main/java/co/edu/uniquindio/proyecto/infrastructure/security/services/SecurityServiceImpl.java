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

    @Override
    public void logout(String token) {
        tokenBlacklist.invalidar(token);
    }
}