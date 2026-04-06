package co.edu.uniquindio.proyecto.aplication.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;

import java.util.List;

public class ConsultarSolicitudesPorEstadoUseCase {

    private final SolicitudRepository solicitudRepository;

    public ConsultarSolicitudesPorEstadoUseCase(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    public List<Solicitud> ejecutar(EstadoSolicitud estado) {
        return solicitudRepository.obtenerPorEstado(estado);
    }
}
