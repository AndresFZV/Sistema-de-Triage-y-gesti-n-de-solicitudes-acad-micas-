package co.edu.uniquindio.proyecto.aplication.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.valueobject.CodigoSolicitud;

public class RechazarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;

    public RechazarSolicitudUseCase(SolicitudRepository solicitudRepository,
                                    UsuarioRepository usuarioRepository,
                                    GestorSolicitudService gestor) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.gestor = gestor;
    }

    public void ejecutar(String codigo, String adminId) {
        Solicitud solicitud = solicitudRepository.obtenerPorCodigo(new CodigoSolicitud(codigo));
        Usuario admin = usuarioRepository.obtenerPorId(adminId);
        gestor.rechazar(solicitud, admin);
        solicitudRepository.guardar(solicitud);
    }
}