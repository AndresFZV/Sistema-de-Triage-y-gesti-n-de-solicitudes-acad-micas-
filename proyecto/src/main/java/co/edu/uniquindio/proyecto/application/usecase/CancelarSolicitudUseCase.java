package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.SolicitudNoEncontradaException;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para cancelar una solicitud.
 *
 * <p>La cancelación solo puede ejecutarla el propio solicitante y únicamente
 * cuando la solicitud está en estado PENDIENTE. La validación de identidad
 * es delegada al agregado {@code Solicitud} a través del
 * {@link GestorSolicitudService}.</p>
 */
@Service
@RequiredArgsConstructor
public class CancelarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;
    private final NotificacionService notificacionService;

    /**
     * Cancela la solicitud indicada por el solicitante original.
     *
     * @param codigo        Código único de la solicitud a cancelar.
     * @param solicitanteId Identificador del solicitante que ejecuta la cancelación.
     * @return Solicitud actualizada en estado CANCELADA.
     * @throws SolicitudNoEncontradaException si no existe la solicitud.
     * @throws UsuarioNoEncontradoException   si no existe el solicitante.
     */
    @Transactional
    public Solicitud ejecutar(String codigo, String solicitanteId) {
        Solicitud solicitud = solicitudRepository.findByCodigo(codigo)
                .orElseThrow(() -> new SolicitudNoEncontradaException(codigo));

        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(solicitanteId));

        gestor.cancelar(solicitud, solicitante);
        Solicitud guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudCancelada(guardada, solicitante);
        return guardada;
    }
}