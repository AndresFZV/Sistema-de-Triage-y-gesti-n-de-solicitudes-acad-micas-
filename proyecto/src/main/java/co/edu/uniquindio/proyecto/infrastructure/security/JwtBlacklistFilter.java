package co.edu.uniquindio.proyecto.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad encargado de validar si un token JWT
 * ha sido invalidado (blacklist).
 *
 * <p>Se ejecuta una vez por cada petición HTTP y verifica el
 * encabezado Authorization en busca de un token Bearer.</p>
 *
 * <p>Si el token se encuentra en la lista negra, la solicitud
 * es rechazada con código 401 (Unauthorized).</p>
 */
@Component
@RequiredArgsConstructor
public class JwtBlacklistFilter extends OncePerRequestFilter {

    private final TokenBlacklist tokenBlacklist;

    /**
     * Procesa cada petición HTTP validando el token JWT.
     *
     * @param request Petición HTTP entrante.
     * @param response Respuesta HTTP.
     * @param filterChain Cadena de filtros.
     * @throws ServletException en caso de error del servlet.
     * @throws IOException en caso de error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (tokenBlacklist.estaInvalidado(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token invalidado");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}