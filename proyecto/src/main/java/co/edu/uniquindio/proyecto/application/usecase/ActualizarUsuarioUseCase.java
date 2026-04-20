package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.Email;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para actualizar los datos de un usuario existente.
 *
 * <p>Orquesta la búsqueda del usuario, la aplicación de los nuevos datos
 * a través del método de dominio y la persistencia del resultado.
 * No contiene reglas de negocio — estas viven en la entidad {@code Usuario}.</p>
 */
@Service
@RequiredArgsConstructor
public class ActualizarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    /**
     * Actualiza el nombre, email y tipo de usuario de un usuario existente.
     *
     * @param id          Identificador externo del usuario a actualizar.
     * @param nombre      Nuevo nombre completo.
     * @param email       Nuevo email válido.
     * @param tipoUsuario Nuevo rol del usuario.
     * @return Usuario actualizado y persistido.
     * @throws UsuarioNoEncontradoException si no existe un usuario con el id dado.
     */
    @Transactional
    public Usuario ejecutar(String id, String nombre, String email, TipoUsuario tipoUsuario) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        Usuario actualizado = usuario.actualizar(nombre, new Email(email), tipoUsuario);
        return usuarioRepository.save(actualizado, null);
    }
}