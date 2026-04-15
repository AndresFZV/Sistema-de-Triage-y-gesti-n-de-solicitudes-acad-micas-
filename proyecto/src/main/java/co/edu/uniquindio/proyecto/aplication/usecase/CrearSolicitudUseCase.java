package co.edu.uniquindio.proyecto.aplication.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;

public class CrearSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;

    public CrearSolicitudUseCase(SolicitudRepository solicitudRepository,
                                 UsuarioRepository usuarioRepository,
                                 GestorSolicitudService gestor) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.gestor = gestor;
    }

    public Solicitud ejecutar(String descripcion, String solicitanteId) {
        Usuario solicitante = usuarioRepository.obtenerPorId(solicitanteId);
        Solicitud solicitud = gestor.registrar(descripcion, solicitante);
        solicitudRepository.guardar(solicitud);
        return solicitud;
    }
}