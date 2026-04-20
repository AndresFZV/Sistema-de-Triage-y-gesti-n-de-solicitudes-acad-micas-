package co.edu.uniquindio.proyecto.infrastructure.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.spec.SecretKeySpec;

/**
 * Configuración de componentes JWT para la aplicación.
 *
 * <p>Define los beans necesarios para codificar y decodificar tokens
 * utilizando un algoritmo simétrico HMAC (HS256).</p>
 *
 * <p>La clave secreta es leída desde la configuración externa
 * ({@code application.properties}).</p>
 */
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Configura cómo se extraen las autoridades desde el token JWT.
     *
     * <p>Utiliza el claim "roles" sin prefijo adicional.</p>
     *
     * @return Convertidor de autenticación JWT.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }

    /**
     * Configura el decodificador JWT.
     *
     * <p>Permite validar y leer tokens firmados con la clave secreta.</p>
     *
     * @return Bean {@link JwtDecoder}.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
                .withSecretKey(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256"))
                .build();
    }

    /**
     * Configura el codificador JWT.
     *
     * <p>Permite generar tokens firmados con la clave secreta.</p>
     *
     * @return Bean {@link JwtEncoder}.
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecret.getBytes()));
    }
}