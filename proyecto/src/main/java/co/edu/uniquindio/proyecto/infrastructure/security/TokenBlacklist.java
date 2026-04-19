package co.edu.uniquindio.proyecto.infrastructure.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacena tokens invalidados en memoria.
 * En producción se reemplazaría por Redis.
 */
@Component
public class TokenBlacklist {

    private final Set<String> tokensInvalidados = ConcurrentHashMap.newKeySet();

    public void invalidar(String token) {
        tokensInvalidados.add(token);
    }

    public boolean estaInvalidado(String token) {
        return tokensInvalidados.contains(token);
    }
}