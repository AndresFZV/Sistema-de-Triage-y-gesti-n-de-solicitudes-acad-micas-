package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;

public interface AsistenteIAService {
    TipoSolicitud sugerirTipo(String descripcion);
    String resumirSolicitud(String descripcion);
    String validarDescripcion(String descripcion);
}