package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import lombok.Getter;

@Getter
public class Usuario {

    private final String id;
    private final String nombre;
    private final Email email;
    private final TipoUsuario tipoUsuario;

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

    public boolean esAdministrativo() {
        return this.tipoUsuario == TipoUsuario.ADMINISTRATIVO;
    }

    public boolean esEstudiante() {
        return this.tipoUsuario == TipoUsuario.ESTUDIANTE;
    }

    public boolean esDocente() {
        return this.tipoUsuario == TipoUsuario.DOCENTE;
    }
}