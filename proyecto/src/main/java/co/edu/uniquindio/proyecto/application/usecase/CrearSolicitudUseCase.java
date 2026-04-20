package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para registrar una nueva solicitud académica.
 *
 * <p>Verifica que el solicitante exista, delega la creación al
 * {@link GestorSolicitudService}, persiste la solicitud y notifica
 * al solicitante del registro exitoso.</p>
 */
@Service
@RequiredArgsConstructor
public class CrearSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;
    private final NotificacionService notificacionService;

    /**
     * Registra una nueva solicitud en estado CLASIFICACION.
     *
     * @param descripcion   Motivo de la solicitud. Debe tener entre 20 y 1000 caracteres.
     * @param solicitanteId Identificador del usuario que registra la solicitud.
     * @return Solicitud creada y persistida.
     * @throws UsuarioNoEncontradoException si no existe el solicitante.
     */
    @Transactional
    public Solicitud ejecutar(String descripcion, String solicitanteId) {
        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(solicitanteId));

        Solicitud solicitud = gestor.registrar(descripcion, solicitante);
        Solicitud guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudCreada(guardada, solicitante);
        return guardada;
    }
}