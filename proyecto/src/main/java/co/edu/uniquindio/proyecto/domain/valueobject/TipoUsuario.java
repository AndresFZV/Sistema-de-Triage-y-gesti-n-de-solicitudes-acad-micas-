package co.edu.uniquindio.proyecto.domain.valueobject;

/**
 * Value Object que representa el rol de un usuario dentro del sistema.
 * Determina qué acciones puede realizar cada usuario sobre las solicitudes.
 *
 * <p>Es un conjunto cerrado de valores: solo existen tres roles posibles
 * y ninguno puede agregarse en tiempo de ejecución.</p>
 */
public enum TipoUsuario {

    /** Persona que registra y puede cancelar sus propias solicitudes. */
    ESTUDIANTE,

    /** Docente del programa académico. Puede registrar solicitudes. */
    DOCENTE,

    /** Personal administrativo. Gestiona el ciclo de vida completo de las solicitudes. */
    ADMINISTRATIVO
}