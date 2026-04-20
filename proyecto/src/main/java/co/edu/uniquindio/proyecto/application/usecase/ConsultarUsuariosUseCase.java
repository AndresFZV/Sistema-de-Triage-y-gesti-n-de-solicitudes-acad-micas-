package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para consultar usuarios y sus solicitudes asociadas.
 *
 * <p>Agrupa las operaciones de lectura sobre el agregado {@code Usuario}.
 * Todas las operaciones son de solo lectura y no modifican estado.</p>
 */
@Service
@RequiredArgsConstructor
public class ConsultarUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;
    private final SolicitudRepository solicitudRepository;

    /**
     * Retorna todos los usuarios registrados en el sistema.
     *
     * @return Lista completa de usuarios.
     */
    @Transactional(readOnly = true)
    public List<Usuario> ejecutar() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su identificador externo.
     *
     * @param id Identificador externo del usuario.
     * @return Optional con el usuario si existe, vacío si no.
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> ejecutarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Retorna todas las solicitudes registradas por un usuario específico.
     *
     * @param usuarioId Identificador del usuario solicitante.
     * @return Lista de solicitudes del usuario.
     * @throws UsuarioNoEncontradoException si no existe el usuario.
     */
    @Transactional(readOnly = true)
    public List<Solicitud> ejecutarSolicitudesPorUsuario(String usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
        return solicitudRepository.findBySolicitanteId(usuarioId);
    }
}