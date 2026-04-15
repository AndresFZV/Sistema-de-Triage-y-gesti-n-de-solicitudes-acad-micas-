package co.edu.uniquindio.proyecto.aplication.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.valueobject.CodigoSolicitud;

public class CancelarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;

    public CancelarSolicitudUseCase(SolicitudRepository solicitudRepository,
                                    UsuarioRepository usuarioRepository,
                                    GestorSolicitudService gestor) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.gestor = gestor;
    }

    public void ejecutar(String codigo, String solicitanteId) {
        Solicitud solicitud = solicitudRepository.obtenerPorCodigo(new CodigoSolicitud(codigo));
        Usuario solicitante = usuarioRepository.obtenerPorId(solicitanteId);
        gestor.cancelar(solicitud, solicitante);
        solicitudRepository.guardar(solicitud);
    }
}