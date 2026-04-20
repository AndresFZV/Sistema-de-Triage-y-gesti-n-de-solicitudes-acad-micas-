package co.edu.uniquindio.proyecto.application.service;

import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.LoginRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.request.RefreshTokenRequest;
import co.edu.uniquindio.proyecto.infrastructure.rest.dto.response.TokenResponse;

/**
 * Contrato del servicio de autenticación y gestión de sesiones JWT.
 *
 * <p>Define las operaciones del ciclo de vida de la autenticación:
 * inicio de sesión, renovación de token y cierre de sesión.
 * Su implementación reside en la capa de infraestructura ya que
 * depende de componentes técnicos como {@code AuthenticationManager}
 * y {@code JwtTokenProvider}.</p>
 */
public interface SecurityService {

    /**
     * Autentica un usuario con sus credenciales y genera un token JWT.
     *
     * @param request DTO con el email y contraseña del usuario.
     * @return Token JWT de acceso junto con su fecha de expiración y roles.
     * @throws org.springframework.security.core.AuthenticationException
     *         si las credenciales son incorrectas.
     */
    TokenResponse login(LoginRequest request);

    /**
     * Renueva el token de acceso usando un refresh token válido.
     *
     * @param request DTO con el refresh token a renovar.
     * @return Nuevo token JWT de acceso con fecha de expiración actualizada.
     */
    TokenResponse refresh(RefreshTokenRequest request);

    /**
     * Invalida el token actual cerrando la sesión del usuario.
     *
     * @param token Token JWT a invalidar.
     */
    void logout(String token);
}