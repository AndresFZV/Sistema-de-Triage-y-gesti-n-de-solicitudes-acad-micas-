package co.edu.uniquindio.proyecto.infrastructure.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Componente encargado de almacenar tokens invalidados.
 *
 * <p>Implementa una lista negra en memoria para evitar el uso
 * de tokens JWT que han sido revocados (por ejemplo, en logout).</p>
 *
 * <p>En un entorno de producción, este mecanismo debería ser
 * reemplazado por una solución distribuida como Redis.</p>
 */
@Component
public class TokenBlacklist {

    private final Set<String> tokensInvalidados = ConcurrentHashMap.newKeySet();

    /**
     * Invalida un token agregándolo a la lista negra.
     *
     * @param token Token a invalidar.
     */
    public void invalidar(String token) {
        tokensInvalidados.add(token);
    }

    /**
     * Verifica si un token ha sido invalidado.
     *
     * @param token Token a verificar.
     * @return true si está invalidado, false en caso contrario.
     */
    public boolean estaInvalidado(String token) {
        return tokensInvalidados.contains(token);
    }
}