package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import lombok.Getter;

/**
 * Entidad que representa a una persona que interactúa con el sistema.
 *
 * <p>Tiene identidad propia definida por su {@code id}. Dos usuarios con
 * el mismo nombre pero distinto id son entidades diferentes.</p>
 *
 * <p>Su rol ({@link TipoUsuario}) determina qué acciones puede realizar
 * sobre las solicitudes. Las validaciones de rol son responsabilidad del
 * {@link GestorSolicitudService}, no de esta entidad.</p>
 */
@Getter
public class Usuario {

    /** Identificador único del usuario. Inmutable una vez creado. */
    private final String id;

    /** Nombre completo del usuario. */
    private final String nombre;

    /** Dirección de correo electrónico validada. */
    private final Email email;

    /** Rol del usuario dentro del sistema. */
    private final TipoUsuario tipoUsuario;

    /**
     * Crea un nuevo usuario validando todos sus campos obligatorios.
     *
     * @param id          Identificador único. No puede ser nulo ni vacío.
     * @param nombre      Nombre completo. No puede ser nulo ni vacío.
     * @param email       Email válido. No puede ser nulo.
     * @param tipoUsuario Rol del usuario. No puede ser nulo.
     * @throws ReglaDominioException si algún campo obligatorio es inválido.
     */
    public Usuario(String id, String nombre, Email email, TipoUsuario tipoUsuario) {
        if (id == null || id.isBlank())
            throw new ReglaDominioException("El id del usuario no puede estar vacío");
        if (nombre == null || nombre.isBlank())
            throw new ReglaDominioException("El nombre no puede estar vacío");
        if (email == null)
            throw new ReglaDominioException("El email es obligatorio");
        if (tipoUsuario == null)
            throw new ReglaDominioException("El tipo de usuario es obligatorio");

        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }

    /**
     * Indica si este usuario tiene rol administrativo.
     *
     * @return {@code true} si el tipo de usuario es {@link TipoUsuario#ADMINISTRATIVO}.
     */
    public boolean esAdministrativo() {
        return this.tipoUsuario == TipoUsuario.ADMINISTRATIVO;
    }

    /**
     * Indica si este usuario tiene rol de estudiante.
     *
     * @return {@code true} si el tipo de usuario es {@link TipoUsuario#ESTUDIANTE}.
     */
    public boolean esEstudiante() {
        return this.tipoUsuario == TipoUsuario.ESTUDIANTE;
    }
}