package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso para buscar usuarios por diferentes criterios.
 *
 * <p>Agrupa consultas de usuarios relacionadas sin modificar estado.
 * Al ser operaciones de solo lectura, están marcadas con
 * {@code readOnly = true} para optimizar el rendimiento.</p>
 */
@Service
@RequiredArgsConstructor
public class BuscarUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;

    /**
     * Busca un usuario por su dirección de email.
     *
     * @param email Email del usuario a buscar.
     * @return Usuario encontrado.
     * @throws UsuarioNoEncontradoException si no existe un usuario con ese email.
     */
    @Transactional(readOnly = true)
    public Usuario porEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(email));
    }

    /**
     * Busca usuarios cuyo nombre contenga el texto indicado.
     *
     * @param nombre Texto parcial a buscar en el nombre.
     * @return Lista de usuarios que coinciden con el criterio.
     */
    @Transactional(readOnly = true)
    public List<Usuario> porNombre(String nombre) {
        return usuarioRepository.findByNombreContaining(nombre);
    }
}