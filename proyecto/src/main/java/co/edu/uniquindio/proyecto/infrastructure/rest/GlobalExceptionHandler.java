package co.edu.uniquindio.proyecto.infrastructure.rest;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.exception.SolicitudNoEncontradaException;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoAutorizadoException;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la capa REST.
 *
 * <p>Centraliza la gestión de errores en la aplicación, capturando
 * excepciones específicas y generando respuestas HTTP estructuradas
 * y consistentes para el cliente.</p>
 *
 * <p>Permite desacoplar la lógica de manejo de errores de los controladores,
 * facilitando el mantenimiento y la estandarización de respuestas.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de datos de entrada.
     *
     * <p>Se activa cuando fallan las anotaciones de validación
     * como {@code @NotBlank}, {@code @Email}, etc.</p>
     *
     * @param ex Excepción de validación.
     * @return Respuesta con los campos inválidos y sus mensajes.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacion(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return buildResponse(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", errores);
    }

    /**
     * Maneja excepciones de reglas de negocio del dominio.
     *
     * @param ex Excepción de dominio.
     * @return Respuesta con código 422 (Unprocessable Entity).
     */
    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<Map<String, Object>> handleReglaDominio(
            ReglaDominioException ex) {

        return buildResponse(HttpStatus.valueOf(422), ex.getMessage(), null);
    }

    /**
     * Maneja argumentos inválidos en la aplicación.
     *
     * @param ex Excepción de argumento inválido.
     * @return Respuesta con código 400 (Bad Request).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    /**
     * Maneja violaciones de integridad en la base de datos.
     *
     * <p>Ejemplo: duplicidad de email u otros campos únicos.</p>
     *
     * @param ex Excepción de integridad de datos.
     * @return Respuesta con código 409 (Conflict).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(
            DataIntegrityViolationException ex) {
        String mensaje = "Ya existe un registro con esos datos";
        if (ex.getMessage() != null && ex.getMessage().contains("EMAIL")) {
            mensaje = "Ya existe un usuario con ese email";
        }
        return buildResponse(HttpStatus.CONFLICT, mensaje, null);
    }

    /**
     * Maneja cualquier excepción no controlada.
     *
     * <p>Actúa como fallback para errores inesperados en el sistema.</p>
     *
     * @param ex Excepción genérica.
     * @return Respuesta con código 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        ex.printStackTrace();
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Por favor intente más tarde",
                null
        );
    }

    /**
     * Construye la estructura estándar de respuesta de error.
     *
     * @param status Código HTTP.
     * @param mensaje Mensaje descriptivo del error.
     * @param errores Detalle de errores (opcional).
     * @return Respuesta estructurada.
     */
    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status, String mensaje, Map<String, String> errores) {

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status.value());
        response.put("mensaje", mensaje);
        response.put("codigo", status.getReasonPhrase());

        if (errores != null)
            response.put("errores", errores);

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Maneja la excepción cuando una solicitud no es encontrada.
     *
     * @param ex Excepción de solicitud no encontrada.
     * @return Respuesta con código 404 (Not Found).
     */
    @ExceptionHandler(SolicitudNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleSolicitudNoEncontrada(
            SolicitudNoEncontradaException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * Maneja la excepción cuando un usuario no es encontrado.
     *
     * @param ex Excepción de usuario no encontrado.
     * @return Respuesta con código 404 (Not Found).
     */
    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(
            UsuarioNoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    /**
     * Maneja la excepción cuando un usuario no está autorizado.
     *
     * @param ex Excepción de usuario no autorizado.
     * @return Respuesta con código 403 (Forbidden).
     */
    @ExceptionHandler(UsuarioNoAutorizadoException.class)
    public ResponseEntity<Map<String, Object>> handleNoAutorizado(
            UsuarioNoAutorizadoException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }
}