package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para eliminar un usuario del sistema.
 *
 * <p>Verifica que el usuario exista antes de eliminarlo para garantizar
 * que el error sea claro y semántico en lugar de silencioso.</p>
 */
@Service
@RequiredArgsConstructor
public class EliminarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;

    /**
     * Elimina el usuario con el identificador indicado.
     *
     * @param id Identificador externo del usuario a eliminar.
     * @throws UsuarioNoEncontradoException si no existe un usuario con ese id.
     */
    @Transactional
    public void ejecutar(String id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        usuarioRepository.deleteById(id);
    }
}