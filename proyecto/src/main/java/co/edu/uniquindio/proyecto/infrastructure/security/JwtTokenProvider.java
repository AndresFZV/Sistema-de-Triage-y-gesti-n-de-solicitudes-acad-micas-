package co.edu.uniquindio.proyecto.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;

/**
 * Componente encargado de la generación de tokens JWT firmados.
 *
 * <p>Utiliza el algoritmo HMAC HS256 para firmar los tokens y define
 * los claims necesarios para la autenticación y autorización.</p>
 *
 * <p>Incluye información como:</p>
 * <ul>
 *     <li>Issuer (emisor)</li>
 *     <li>Usuario autenticado (subject)</li>
 *     <li>Roles del usuario</li>
 *     <li>Fechas de emisión y expiración</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtEncoder encoder;

    /**
     * Genera un token JWT como cadena de texto.
     *
     * @param username Identificador del usuario (subject).
     * @param roles Lista de roles o autoridades.
     * @param issuedAt Fecha de emisión del token.
     * @param expiresAt Fecha de expiración del token.
     * @return Token JWT firmado.
     */
    public String generateTokenAsString(String username, Collection<String> roles,
                                        Instant issuedAt, Instant expiresAt) {
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("solicitudes-app")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("roles", roles)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}